package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends FilmorateController<User> {
    private final Map<Long, User> users = new HashMap<>();
    private Long counter = 0L;

    public Long generateId() {
        return ++counter;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User create(@Valid @RequestBody User user) {
        log.debug("Запрос на добавление пользователя: {}", user);
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        Long userId = generateId();
        user.setId(userId);
        users.put(userId, user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User update(@Valid @RequestBody User user) {
        log.debug("Запрос на обновление пользователя: {}", user);
        if (user.getId() < 1) {
            throw new ValidationException("Неверный идентификатор пользователя");
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }
}
