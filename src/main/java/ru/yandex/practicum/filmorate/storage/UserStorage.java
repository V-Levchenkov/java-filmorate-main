package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;

public interface UserStorage {

    Collection<User> getAll();

    User getUser(Long userId);

    User create(@Valid @RequestBody User user);

    User update(@Valid @RequestBody User user);
}
