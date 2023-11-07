package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film addFilm(Film film);

    Collection<Film> getAllFilms();

    Film updateFilm(Film film);

    void deleteFilm(int filmId);

    Film getFilm(int filmId);

    void checkFilmInData(int filmId);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);
}
