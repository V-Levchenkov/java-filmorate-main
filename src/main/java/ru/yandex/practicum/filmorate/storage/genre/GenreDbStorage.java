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

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(Long id) {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        return jdbcTemplate.query(sql, this::makeGenre, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException(String.format("Жанр c id %s не найден.", id)));
    }

    @Override
    public List<Genre> getGenresByFilm(Long filmId) {
        String sql = "SELECT * FROM GENRES LEFT JOIN FILM_GENRES FG ON GENRES.GENRE_ID = FG.GENRE_ID WHERE FILM_ID = ?";
        return jdbcTemplate.query(sql, this::makeGenre, filmId);
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM GENRES", this::makeGenre);
    }

    @Override
    public void assignGenreToFilm(Long filmId, List<Genre> genres) {
        for (Genre genre : genres) {
            genre.setName(getGenreById(genre.getId()).getName());
            String sql = "MERGE INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.update(sql, filmId, genre.getId());
        }
    }

    public void deleteGenresByFilm(Long filmId) {
        String sql = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Genre makeGenre(ResultSet rs, int RowNum) throws SQLException {
        Long id = rs.getLong("GENRE_ID");
        String name = rs.getString("GENRE");
        return new Genre(id, name);
    }
}