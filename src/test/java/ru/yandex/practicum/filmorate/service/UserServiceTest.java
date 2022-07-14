package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:beforeEachTest.sql")
class UserServiceTest {
    private final UserStorage userStorage;
    private final UserService userService;

    User createTestUserEntity() {
        User user = new User();
        long timestamp = Instant.now().getEpochSecond();
        user.setLogin(String.format("testLogin_%s", timestamp));
        user.setName(String.format("testName %s", timestamp));
        user.setEmail(String.format("%s@email.com", timestamp));
        user.setBirthday(Date.valueOf("2000-01-01"));
        return user;
    }

    @Test
    void getUsersTest() {
        assertThat(userService.getUsers().isEmpty());
    }

    @Test
    void createUserWithEmptyNameTest() {
        User user = createTestUserEntity();
        user.setName("");
        userStorage.create(user);
        assertThat(userService.getUser(user.getId()).getName().equals(user.getLogin()));
    }

    @Test
    void createUserWithLoginContainingWhitespaceTest() {
        User user = createTestUserEntity();
        user.setLogin("test login");
        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    void getUserByIdWithWrongIdTest() {
        assertThrows(NotFoundException.class, () -> userService.getUser(666L));
    }

    @Test
    void updateUserWithLoginContainingWhitespaceTest() {
        User user = createTestUserEntity();
        user.setLogin("test login");
        userStorage.create(user);
        assertThrows(ValidationException.class, () -> userService.updateUser(user));
    }

    @Test
    void updateUserWithEmptyNameTest() {
        User user = createTestUserEntity();
        user.setName("");
        userStorage.create(user);
        assertThat(userService.getUser(user.getId()).getName().equals(user.getLogin()));
    }

    @Test
    void deleteUserWithWrongIdTest() {
        assertThrows(NotFoundException.class, () -> userService.deleteUser(666L));
    }

    @Test
    void addFriendTest() {
        User user = userStorage.create(createTestUserEntity());
        assertThrows(NotFoundException.class, () -> userService.addFriend(user.getId(), 666L));
        assertThrows(NotFoundException.class, () -> userService.addFriend(666L, user.getId()));
    }

    @Test
    void removeFriendTest() {
        User user = userStorage.create(createTestUserEntity());
        assertThrows(NotFoundException.class, () -> userService.deleteFriend(user.getId(), 666L));
        assertThrows(NotFoundException.class, () -> userService.deleteFriend(666L, user.getId()));
    }

    @Test
    void getFriendsTest() {
        assertThrows(NotFoundException.class, () -> userService.getUser(666L));
    }

    @Test
    void getCommonFriendsTest() {
        User user1 = userService.createUser(createTestUserEntity());
        User user2 = userService.createUser(createTestUserEntity());
        User friend1 = userService.createUser(createTestUserEntity());
        User friend2 = userService.createUser(createTestUserEntity());
        User commonFriend = userService.createUser(createTestUserEntity());

        userService.addFriend(user1.getId(), friend1.getId());
        userService.addFriend(user2.getId(), friend2.getId());
        userService.addFriend(user1.getId(), commonFriend.getId());
        userService.addFriend(user2.getId(), commonFriend.getId());

        assertThat(userService.getCommonFriends(user1.getId(), user2.getId())).hasSize(1);
        assertThat(userService.getCommonFriends(user1.getId(), user2.getId())).isEqualTo(List.of(commonFriend));
    }

}