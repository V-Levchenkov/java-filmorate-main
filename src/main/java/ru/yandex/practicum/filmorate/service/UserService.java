package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;


@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        log.debug("Запрос на добавление пользователя: {}", user);
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public void deleteUser(Long id) {
        validateUserId(id);
        userStorage.deleteUser(id);
    }

    public User updateUser(User user) {
        log.debug("Запрос на обновление пользователя: {}", user);
        validateUserId(user.getId());
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getUserFriends(Long userId) {
        validateUserId(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.info("Запрос общих друзей у пользователей {} и {}", userId, otherId);
        validateUserId(userId);
        validateUserId(otherId);
        Set<User> userFriends = new HashSet<>(getUserFriends(userId));
        Set<User> otherUserFriends = new HashSet<>(getUserFriends(otherId));
        userFriends.retainAll(otherUserFriends);
        return new ArrayList<>(userFriends);
    }

    public User getUser(Long userId) {
        validateUserId(userId);
        return userStorage.getUserById(userId);
    }

    private void validateUserId(Long id) {
        if (userStorage.getUserById(id) == null) {
            throw new NotFoundException(String.format("Пользователь c id %s не найден.", id));
        }
        validateNameAndLogin(userStorage.getUserById(id));
    }

    private void validateNameAndLogin(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Ошибка: поле login не должно содержать пробелы");
        }
    }
}
