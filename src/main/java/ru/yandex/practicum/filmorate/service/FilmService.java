package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class FilmService {

    private final Map<Integer, Film> films = new HashMap<>();
    private int idFilm = 0;

    public Film addFilm(Film film) {
        realiseDateValidation(film);
        Film filmToAdd = film.toBuilder().id(++idFilm).build();
        films.put(filmToAdd.getId(), filmToAdd);
        return filmToAdd;
    }

    public Collection<Film> getAllFilms() {
        return films.values();
    }

    public Film updateFilm(@Valid @RequestBody Film film) {
        realiseDateValidation(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new NotFoundException("Фильм не найден");
        }
    }

    public void realiseDateValidation(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
    }
}
