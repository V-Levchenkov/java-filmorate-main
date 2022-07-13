package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long id) {
        validateFilmId(id);
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film film) {
        validateReleaseDate(film);
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        validateFilmId(film.getId());
        validateReleaseDate(film);
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        validateFilmId(filmId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        validateFilmId(filmId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getHighlyRatedFilms(Long count) {
        return filmStorage.getHighlyRatedFilms(count);
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
