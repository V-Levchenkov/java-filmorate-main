package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.debug("Получен запрос GET /users");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id){
        log.debug("Получен запрос GET /users по id {}", id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable("id") Long id){
        log.debug("Получен запрос GET /friends для пользователя с id {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Long userId, @PathVariable("otherId") Long otherUserId){
        log.debug("Получен запрос GET (getCommonFriends) для пользователей с id {} и {}", userId, otherUserId);
        return userService.getCommonFriends(userId, otherUserId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.debug("Получен запрос POST (createUser). Добавлен пользователь: {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Получен запрос PUT (updateUser). Добавлен пользователь: {}", user);
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        userService.addFriend(userId, friendId);
        log.debug("Получен запрос PUT (addFriend). Пользователь {} добавлен в друзья к пользователю {}"
                , userId, friendId);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable("id") Long id){
        log.debug("Получен запрос DELETE /users по id {}", id);
        userService.deleteUser(id);
    }
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
        userService.removeFriend(userId, friendId);
        log.debug("Получен запрос DELETE (deleteFriend). Пользователь {} удален из друзей у пользователя {}"
                , userId, friendId);
    }
}