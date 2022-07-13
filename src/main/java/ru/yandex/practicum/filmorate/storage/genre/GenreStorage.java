package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre getGenreById(Long id);

    List<Genre> getGenresByFilm(Long filmId);

    List<Genre> getAllGenres();

    void assignGenreToFilm(Long filmId, List<Genre> genres);

    void deleteGenresByFilm(Long filmId);
}

