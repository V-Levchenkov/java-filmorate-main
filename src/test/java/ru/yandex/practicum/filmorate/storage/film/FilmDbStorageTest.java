package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.Date;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:beforeEachTest.sql")
class FilmDbStorageTest {
    private final FilmStorage filmStorage;
    private final UserDbStorage userStorage;

    Film createTestFilmEntity() {
        Film film = new Film();
        long timestamp = Instant.now().getEpochSecond();
        film.setName(String.format("testName %s", timestamp));
        film.setDescription(String.format("testDescription %s", timestamp));
        film.setReleaseDate(Date.valueOf("2000-01-01"));
        film.setDuration(100);
        film.setMpa(new MpaRating(1L, "G"));
        film.setGenres(List.of(new Genre(1L, "Комедия")));
        return film;
    }

    User createTestUserEntity() {
        User user = new User();
        long timestamp = Instant.now().getEpochSecond();
        user.setLogin(String.format("testLogin %s", timestamp));
        user.setName(String.format("testName %s", timestamp));
        user.setEmail(String.format("%s@email.com", timestamp));
        user.setBirthday(Date.valueOf("2000-01-01"));
        return user;
    }

    @Test
    void getFilmByIdTest() {
        Film film = filmStorage.create(createTestFilmEntity());
        Long filmId = film.getId();
        assertThat(filmStorage.getFilmById(filmId)).isEqualTo(film);
    }

    @Test
    void getFilmsTest() {
        Film film1 = filmStorage.create(createTestFilmEntity());
        Film film2 = filmStorage.create(createTestFilmEntity());
        Collection<Film> allFilms = filmStorage.getFilms();
        assertThat(allFilms).contains(film1, film2);
        assertThat(allFilms).hasSize(2);
    }

    @Test
    void createTest() {
        Film film = filmStorage.create(createTestFilmEntity());
        Collection<Film> allFilms = filmStorage.getFilms();
        assertThat(allFilms).contains(film);
        assertThat(allFilms).hasSize(1);
    }

    @Test
    void updateTest() {
        Film film = filmStorage.create(createTestFilmEntity());
        film.setName("updatedTestName");
        film.setDescription("updatedTestDescription");
        film.setReleaseDate(Date.valueOf("2002-02-02"));
        film.setDuration(666);
        filmStorage.update(film);
        Long filmId = film.getId();
        assertThat(filmStorage.getFilmById(filmId))
                .hasFieldOrPropertyWithValue("name", "updatedTestName")
                .hasFieldOrPropertyWithValue("description", "updatedTestDescription")
                .hasFieldOrPropertyWithValue("releaseDate", Date.valueOf("2002-02-02"))
                .hasFieldOrPropertyWithValue("duration", 666);
        assertThat(filmStorage.getFilms()).hasSize(1);
    }

    @Test
    void deleteFilmTest() {
        Film film = filmStorage.create(createTestFilmEntity());
        Long filmId = film.getId();
        filmStorage.deleteFilm(filmId);
        assertThat(filmStorage.getFilms()).doesNotContain(film);
        assertThat(filmStorage.getFilms()).hasSize(0);
    }

    @Test
    void getHighlyRatedFilmsTest() {
        Film film1 = filmStorage.create(createTestFilmEntity());
        Film film2 = filmStorage.create(createTestFilmEntity());
        User user = userStorage.create(createTestUserEntity());
        filmStorage.addLike(film2.getId(), user.getId());
        assertThat(filmStorage.getHighlyRatedFilms(10L)).isEqualTo(List.of(film2, film1));
    }

    @Test
    void addAndGetLikeTest() {
        Film film = filmStorage.create(createTestFilmEntity());
        Long filmId = film.getId();
        User user = userStorage.create(createTestUserEntity());
        Long userId = user.getId();
        filmStorage.addLike(filmId, userId);
        assertThat(filmStorage.getLikes(filmId)).contains(userId);
        assertThat(filmStorage.getLikes(filmId)).hasSize(1);
    }

    @Test
    void removeLikeTest() {
        Film film = filmStorage.create(createTestFilmEntity());
        Long filmId = film.getId();
        User user = userStorage.create(createTestUserEntity());
        Long userId = user.getId();
        filmStorage.addLike(filmId, userId);
        filmStorage.removeLike(filmId, userId);
        assertThat(filmStorage.getLikes(filmId)).doesNotContain(userId);
        assertThat(filmStorage.getLikes(filmId)).hasSize(0);
    }
}