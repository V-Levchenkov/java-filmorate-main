package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String GET_GENRE_BY_ID = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
    private static final String GET_GENRES_BY_FILM = "SELECT * FROM GENRES LEFT JOIN FILM_GENRES FG ON GENRES.GENRE_ID = FG.GENRE_ID WHERE FILM_ID = ?";
    private static final String GET_ALL_GENRES = "SELECT * FROM GENRES";
    private static final String ASSIGN_GENRE_TO_FILM = "MERGE INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
    private static final String DELETE_GENRES_BY_FILM = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";


    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(Long id) {
        return jdbcTemplate.query(GET_GENRE_BY_ID, this::makeGenre, id).stream().findAny().orElseThrow(() -> new NotFoundException(String.format("Жанр c id %s не найден.", id)));
    }

    @Override
    public List<Genre> getGenresByFilm(Long filmId) {
        return jdbcTemplate.query(GET_GENRES_BY_FILM, this::makeGenre, filmId);
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(GET_ALL_GENRES, this::makeGenre);
    }

    @Override
    public void assignGenreToFilm(Long filmId, List<Genre> genres) {
        for (Genre genre : genres) {
            genre.setName(getGenreById(genre.getId()).getName());
            jdbcTemplate.update(ASSIGN_GENRE_TO_FILM, filmId, genre.getId());
        }
    }

    public void deleteGenresByFilm(Long filmId) {
        jdbcTemplate.update(DELETE_GENRES_BY_FILM, filmId);
    }

    private Genre makeGenre(ResultSet rs, int RowNum) throws SQLException {
        Long id = rs.getLong("GENRE_ID");
        String name = rs.getString("GENRE");
        return new Genre(id, name);
    }
}