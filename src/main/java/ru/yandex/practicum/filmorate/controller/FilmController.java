package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма");
        Film addedFilm = filmService.addFilm(film);
        log.info("Фильм успешно добавлен. ID фильма: {}", addedFilm.getId());
        return addedFilm;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен запрос на получение всех фильмов");
        Collection<Film> allFilms = filmService.getAllFilms();
        log.info("Получен список из фильмов. Текущее количество: {}", allFilms.size());
        return allFilms;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма");
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Фильм успешно обновлен. ID фильма: {}", updatedFilm.getId());
        return updatedFilm;
    }
}
