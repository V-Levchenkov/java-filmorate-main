package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends FilmorateController<Film> {
    public static final int DESCRIPTION_MAX_LENGTH = 200;
    public static final LocalDate FILMOGRAPHY_START_DATE = LocalDate.of(1895, 12, 28);
    private Map<Long, Film> films = new HashMap<>();
    private Long counter = 0L;

    public Long generateId() {
        return ++counter;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film create(@Valid @RequestBody Film film) {
        validateFilm(film);
        Long filmId = generateId();
        film.setId(filmId);
        films.put(filmId, film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(@Valid @RequestBody Film film) {
        validateFilm(film);
        if (film.getId() < 1) {
            throw new ValidationException("Неверный идентификатор фильма");
        }
        films.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    private static void validateFilm(Film film) {
        try {
            if (film.getDescription().length() > DESCRIPTION_MAX_LENGTH) {
                throw new ValidationException("Максимальная длина описания — 200 символов");
            }
            if (film.getReleaseDate().isBefore(FILMOGRAPHY_START_DATE)) {
                throw new ValidationException("Дата релиза не может быть раньше даты рождения кино");
            }
        } catch (ValidationException e) {
            log.error(e.getMessage());
            throw new ValidationException(e.getMessage());
        }
    }
}
