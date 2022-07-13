package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.debug("Получен запрос GET /films");
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") Long id){
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getHighlyRatedFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Long count
    ){
        if (count <= 0) {
            throw new IncorrectParameterException("count");
        }
        log.debug("Получен запрос GET для /films/popular (getHighlyRatedFilms)");
        return filmService.getHighlyRatedFilms(count);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug("Получен запрос POST. Добавлен фильм: {}", film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Получен запрос PUT. Добавлен фильм: {}", film);
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long filmId, @PathVariable("userId") Long userId) {
        filmService.addLike(filmId, userId);
        log.debug("Получен запрос PUT (addLike). От ользователя {} поставлен лайк к фильму {}"
                , userId, filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Long filmId, @PathVariable("userId") Long userId) {
        if (filmId <= 0) {
            throw new IncorrectParameterException("id");
        }
        if (userId <= 0) {
            throw new IncorrectParameterException("userId");
        }
        filmService.removeLike(filmId, userId);
        log.debug("Получен запрос DELETE (removeLike). Удален лайк пользофвталея {} к фильму {}", filmId, userId);
    }
}
