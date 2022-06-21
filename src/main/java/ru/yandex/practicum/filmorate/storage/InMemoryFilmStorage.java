package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long counter = 0L;

    public Long generateId() {
        return ++counter;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film getFilm(Long filmId) {
        return films.get(filmId);
    }

    @Override
    public Film create(Film film) {
        Long filmId = generateId();
        film.setId(filmId);
        films.put(filmId, film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(@Valid @RequestBody Film film) {
        films.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }
}
