package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(Long id) {
        validateGenreId(id);
        return genreStorage.getGenreById(id);
    }

    private void validateGenreId(Long id) {
        if (genreStorage.getGenreById(id) == null) {
            throw new NotFoundException(String.format("Жанр c id %s не найден.", id));
        }
    }
}
