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
    private static final String GET_USER_BY_ID = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String GET_ALL_USERS = "SELECT * FROM USERS";
    private static final String CREATE_USER = "INSERT INTO USERS (USER_EMAIL, USER_LOGIN, USER_NAME, BIRTHDATE) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "MERGE INTO USERS (USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, BIRTHDATE) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_USER = "DELETE FROM USERS WHERE USER_ID = ?";
    private static final String GET_FRIENDS = "SELECT FRIEND_ID FROM USER_FRIENDS WHERE USER_ID = ?";
    private static final String ADD_FRIEND = "INSERT INTO USER_FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
    private static final String DELETE_FRIEND = "DELETE FROM USER_FRIENDS WHERE USER_ID = ? AND FRIEND_ID =?";

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User getUserById(Long id) {
        return jdbcTemplate.query(GET_USER_BY_ID, this::makeUser, id).stream().findAny().orElseThrow(() -> new NotFoundException(String.format("Пользователь c id %s не найден.", id)));
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbcTemplate.query(GET_ALL_USERS, this::makeUser);
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(CREATE_USER, new String[]{"USER_ID"});
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
        jdbcTemplate.update(UPDATE_USER, user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return getUserById(user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        jdbcTemplate.update(DELETE_USER, id);
    }

    @Override
    public List<User> getFriends(Long id) {
        List<Long> friends = jdbcTemplate.queryForList(GET_FRIENDS, Long.class, id);
        return friends.stream().map(this::getUserById).collect(Collectors.toList());
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbcTemplate.update(ADD_FRIEND, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbcTemplate.update(DELETE_FRIEND, userId, friendId);
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
