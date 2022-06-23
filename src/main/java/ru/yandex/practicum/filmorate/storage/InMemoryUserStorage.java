package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
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
    public User getUser(Long userId) {
        return users.get(userId);
    }

    @Override
    public User create(User user) {
        Long userId = generateId();
        user.setId(userId);
        users.put(userId, user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }
}
