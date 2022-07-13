package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film getFilmById(Long id);

    Collection<Film> getFilms();

    Film create(Film film);

    Film update(Film film);

    void deleteFilm(Long id);

    List<Film> getHighlyRatedFilms(Long count);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Long> getLikes(Long filmId);
}
