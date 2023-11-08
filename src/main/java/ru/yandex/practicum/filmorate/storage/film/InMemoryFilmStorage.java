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
import java.util.Set;

@Component
@Getter
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idFilm = 0;

    @Override
    public Film add(Film film) {
        realiseDateValidation(film);
        Film filmToAdd = film.toBuilder().id(++idFilm).build();
        filmToAdd.checkLikesList();
        films.put(filmToAdd.getId(), filmToAdd);
        return filmToAdd;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film get(int filmId) {
        checkFilmInData(filmId);
        return films.get(filmId);
    }

    @Override
    public Film update(Film film) {
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
    public void delete(int filmId) {
        if (films.containsKey(filmId)) {
            films.remove(filmId);
        } else {
            throw new NotFoundException("Фильм не найден");
        }
    }

    public void realiseDateValidation(Film film) {
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза пуста");
        } else {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
            }
        }
    }

    public void checkFilmInData(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с ID: " + filmId + "не найден");
        }
    }

    private void updateLikesList(int filmId, int userId, boolean add) {
        Film film = get(filmId);
        Set<Integer> likes = film.getLikes();

        if (add) {
            if (!likes.add(userId)) {
                throw new NotFoundException("Пользователь уже лайкнул этот фильм");
            }
        } else {
            if (!likes.remove(userId)) {
                throw new NotFoundException("Пользователь не найден в списке лайков");
            }
        }
    }

    public void addLike(int filmId, int userId) {
        updateLikesList(filmId, userId, true);
    }

    public void deleteLike(int filmId, int userId) {
        updateLikesList(filmId, userId, false);
    }
}
