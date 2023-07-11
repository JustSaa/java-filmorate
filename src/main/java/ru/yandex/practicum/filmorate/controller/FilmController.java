package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idFilm = 0;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {
        log.info("Получен запрос на добавление фильма");
        //Validation
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                log.error("Ошибка валидации поля '{}': {}", error.getField(), error.getDefaultMessage());
            }
            throw new ru.yandex.practicum.filmorate.exeption.ValidationException("Ошибка валидации данных фильма");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
        Film filmToAdd = film.toBuilder().id(++idFilm).build();
        films.put(filmToAdd.getId(), filmToAdd);
        return filmToAdd;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен запрос на получение всех фильмов");
        return films.values();
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {
        log.info("Получен запрос на обновление фильма");
        //Validation
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                log.error("Ошибка валидации поля '{}': {}", error.getField(), error.getDefaultMessage());
            }
            throw new ru.yandex.practicum.filmorate.exeption.ValidationException("Ошибка валидации данных фильма");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new NotFoundException("Фильм не найден");
        }
    }
}
