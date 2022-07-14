package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.Date;
import java.time.Instant;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:beforeEachTest.sql")
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    User createTestUserEntity() {
        User user = new User();
        long timestamp = Instant.now().getEpochSecond();
        user.setLogin(String.format("testLogin %s", timestamp));
        user.setName(String.format("testName %s", timestamp));
        user.setEmail(String.format("%s@email.com", timestamp));
        user.setBirthday(Date.valueOf("2000-01-01"));
        return user;
    }

    @Test
    public void findUserByIdTest() {
        User user = userStorage.create(createTestUserEntity());
        Long userId = user.getId();
        assertThat(userStorage.getUserById(userId)).isEqualTo(user);
    }

    @Test
    void getAllUsersTest() {
        User user1 = userStorage.create(createTestUserEntity());
        User user2 = userStorage.create(createTestUserEntity());
        Collection<User> allUsers = userStorage.getAllUsers();
        assertThat(allUsers).contains(user1, user2);
        assertThat(allUsers).hasSize(2);
    }

    @Test
    void createTest() {
        User user = userStorage.create(createTestUserEntity());
        Collection<User> allUsers = userStorage.getAllUsers();
        assertThat(allUsers).contains(user);
        assertThat(allUsers).hasSize(1);
    }

    @Test
    void updateTest() {
        User user = userStorage.create(createTestUserEntity());
        user.setLogin("updatedLogin");
        user.setName("updatedName");
        user.setEmail("update@email.com");
        user.setBirthday(Date.valueOf("2002-02-02"));
        userStorage.update(user);
        Long userId = user.getId();
        assertThat(userStorage.getUserById(userId))
                .hasFieldOrPropertyWithValue("login", "updatedLogin")
                .hasFieldOrPropertyWithValue("name", "updatedName")
                .hasFieldOrPropertyWithValue("email", "update@email.com")
                .hasFieldOrPropertyWithValue("birthday", Date.valueOf("2002-02-02"));
        assertThat(userStorage.getAllUsers()).hasSize(1);
    }

    @Test
    void deleteUserTest() {
        User user = userStorage.create(createTestUserEntity());
        Long userId = user.getId();
        userStorage.deleteUser(userId);
        assertThat(userStorage.getAllUsers()).doesNotContain(user);
        assertThat(userStorage.getAllUsers()).hasSize(0);
    }

    @Test
    void addAndGetFriendsTest() {
        User user = userStorage.create(createTestUserEntity());
        Long userId = user.getId();
        User friend = userStorage.create(createTestUserEntity());
        Long friendId = friend.getId();
        userStorage.addFriend(userId, friendId);
        Collection<User> allUsers = userStorage.getAllUsers();
        assertThat(userStorage.getFriends(userId)).contains(friend);
        assertThat(allUsers).hasSize(2);
    }

    @Test
    void removeFriendTest() {
        User user = userStorage.create(createTestUserEntity());
        Long userId = user.getId();
        Long friendId = user.getId();
        User friend = userStorage.create(createTestUserEntity());
        userStorage.addFriend(userId, friendId);
        userStorage.removeFriend(userId, friendId);
        assertThat(userStorage.getFriends(userId)).doesNotContain(friend);
        assertThat(userStorage.getFriends(userId)).hasSize(0);
        assertThat(userStorage.getAllUsers()).hasSize(2);
    }
}