package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    Film addFilm(Film film);

    Film getFilmById(int filmId);

    Collection<Film> getAllFilms();

    Film updateFilm(Film film);

    void deleteFilm(int filmId);

    List<Film> getTop(int topN);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);
}
