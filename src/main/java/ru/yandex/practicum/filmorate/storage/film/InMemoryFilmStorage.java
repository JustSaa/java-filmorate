package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idFilm = 0;

    @Override
    public Film addFilm(Film film) {
        realiseDateValidation(film);
        Film filmToAdd = film.toBuilder().id(++idFilm).build();
        filmToAdd.checkLikesList();
        films.put(filmToAdd.getId(), filmToAdd);
        return filmToAdd;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film updateFilm(Film film) {
        realiseDateValidation(film);
        film.checkLikesList();
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new NotFoundException("Фильм не найден");
        }
    }

    @Override
    public void deleteFilm(int filmId) {
        if (films.containsKey(filmId)) {
            films.remove(filmId);
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
