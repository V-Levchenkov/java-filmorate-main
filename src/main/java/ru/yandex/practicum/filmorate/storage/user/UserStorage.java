package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    User getUserById(Long id);

    Collection<User> getAllUsers();

    User create(User user);

    User update(User user);

    void deleteUser(Long id);

    void addFriend(Long userId, Long friendId);

    List<User> getFriends(Long id);

    void removeFriend(Long userId, Long friendId);

}
