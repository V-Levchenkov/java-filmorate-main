package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User getUserById(Long id) {
        String sql = "SELECT * FROM USERS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, this::makeUser, id).stream().findAny().orElseThrow(() -> new NotFoundException(String.format("Пользователь c id %s не найден.", id)));
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO USERS (USER_EMAIL, USER_LOGIN, USER_NAME, BIRTHDATE) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            final Date birthday = user.getBirthday();
            if (birthday == null) {
                stmt.setNull(4, Types.DATE);
            } else {
                stmt.setDate(4, birthday);
            }
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "MERGE INTO USERS (USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, BIRTHDATE) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return getUserById(user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        String sql = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<User> getFriends(Long id) {
        String sql = "SELECT FRIEND_ID FROM USER_FRIENDS WHERE USER_ID = ?";
        List<Long> friends = jdbcTemplate.queryForList(sql, Long.class, id);
        return friends.stream().map(this::getUserById).collect(Collectors.toList());
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO USER_FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM USER_FRIENDS WHERE USER_ID = ? AND FRIEND_ID =?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("USER_ID");
        String email = rs.getString("USER_EMAIL");
        String login = rs.getString("USER_LOGIN");
        String name = rs.getString("USER_NAME");
        Date birthday = rs.getDate("BIRTHDATE");
        return new User(id, email, login, name, birthday);
    }
}
