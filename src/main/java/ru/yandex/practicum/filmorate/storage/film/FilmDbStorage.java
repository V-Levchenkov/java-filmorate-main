package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private static final String GET_FILM_BY_ID = "SELECT * FROM FILMS AS F " + "LEFT JOIN FILM_GENRES FG ON F.FILM_ID = FG.FILM_ID " + "LEFT JOIN MPA_RATING MR ON MR.RATING_ID = F.MPA_RATING_ID " + "LEFT JOIN GENRES G ON G.GENRE_ID = FG.GENRE_ID " + "LEFT JOIN FILM_LIKES FL on F.FILM_ID = FL.FILM_ID " + "WHERE F.FILM_ID = ?";
    private static final String GET_ALL_FILMS = "SELECT * FROM FILMS AS F " + "LEFT JOIN FILM_GENRES FG ON F.FILM_ID = FG.FILM_ID " + "LEFT JOIN MPA_RATING MR ON MR.RATING_ID = F.MPA_RATING_ID " + "LEFT JOIN GENRES G ON G.GENRE_ID = FG.GENRE_ID ";
    private static final String CREATE_FILM = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_RATING_ID) " + "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM = "MERGE INTO FILMS (FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION," + "MPA_RATING_ID) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String DELETE_FILM = "DELETE FROM FILMS WHERE FILM_ID = ?";
    private static final String GET_HIGHLY_RATED_FILMS = "SELECT F.FILM_ID FROM FILM_LIKES FL " + "RIGHT JOIN FILMS F on F.FILM_ID = FL.FILM_ID " + "GROUP BY F.FILM_ID ORDER BY COUNT(FL.FILM_ID) DESC LIMIT ?";
    private static final String ADD_LIKE = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String GET_LIKES = "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID = ?";
    private static final String REMOVE_LIKE = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID =?";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    public Film getFilmById(Long id) {
        return jdbcTemplate.query(GET_FILM_BY_ID, this::makeFilm, id).stream().findAny().orElseThrow(() -> new NotFoundException(String.format("Фильм c id %s не найден.", id)));
    }

    @Override
    public Collection<Film> getFilms() {
        return jdbcTemplate.query(GET_ALL_FILMS, this::makeFilm);
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(CREATE_FILM, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, film.getReleaseDate());
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        Long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);

        if (film.getGenres() != null) {
            List<Genre> genres = removeGenreDuplicates(film);
            genreStorage.assignGenreToFilm(filmId, genres);
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update(UPDATE_FILM, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());

        if (film.getGenres() != null) {
            List<Genre> genres = removeGenreDuplicates(film);
            genreStorage.deleteGenresByFilm(film.getId());
            genreStorage.assignGenreToFilm(film.getId(), genres);
        }

        return film;
    }

    @Override
    public void deleteFilm(Long id) {
        jdbcTemplate.update(DELETE_FILM, id);
    }

    @Override
    public List<Film> getHighlyRatedFilms(Long count) {
        List<Long> filmIds = jdbcTemplate.queryForList(GET_HIGHLY_RATED_FILMS, Long.class, count);
        return filmIds.stream().map(this::getFilmById).collect(Collectors.toList());
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        System.out.println(filmId + userId);
        jdbcTemplate.update(ADD_LIKE, filmId, userId);
    }

    @Override
    public List<Long> getLikes(Long filmId) {
        return jdbcTemplate.queryForList(GET_LIKES, Long.class, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbcTemplate.update(REMOVE_LIKE, filmId, userId);
    }

    private List<Genre> removeGenreDuplicates(Film film) {
        film.setGenres(film.getGenres().stream().distinct().collect(Collectors.toList()));
        return film.getGenres();
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("FILM_ID");
        String name = rs.getString("FILM_NAME");
        String description = rs.getString("DESCRIPTION");
        Date releaseDate = rs.getDate("RELEASE_DATE");
        int duration = rs.getInt("DURATION");
        MpaRating mpa = new MpaRating(rs.getLong("MPA_RATING_ID"), rs.getString("RATING_VALUE"));
        List<Genre> genre = genreStorage.getGenresByFilm(id);
        return new Film(id, name, description, releaseDate, duration, mpa, genre);
    }
}
