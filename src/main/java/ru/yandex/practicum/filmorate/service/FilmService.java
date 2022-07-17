package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> getAll() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        validateReleaseDate(film);
        return filmStorage.create(film);
    }

    public Film updateFilm(@Valid @RequestBody Film film) {
        validateFilmId(film.getId());
        validateReleaseDate(film);
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        validateFilmId(filmId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (getFilm(filmId) != null && userService.getUser(userId) != null) {
            filmStorage.removeLike(filmId, userId);
        }
    }

    public List<Film> getPopularFilmList(Long count) {
        return filmStorage.getHighlyRatedFilms(count);
    }

    public Film getFilm(Long filmId) {
        validateFilmId(filmId);
        return filmStorage.getFilmById(filmId);
    }

    private void validateFilmId(Long id) {
        if (filmStorage.getFilmById(id) == null) {
            throw new NotFoundException(String.format("Фильм c id %s не найден.", id));
        }
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().toLocalDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Указана некорректная дата выпуска фильма.");
        }
    }
}
