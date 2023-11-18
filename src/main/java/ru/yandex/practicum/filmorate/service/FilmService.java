package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmDbStorage filmStorage;

    @Autowired
    public FilmService(FilmDbStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getById(filmId);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public void deleteFilm(int filmId) {
        filmStorage.delete(filmId);
    }

    public List<Film> getTop(int topN) {
        Map<Film, Integer> filmsLikes = filmStorage.getLikesForFilms();

        return filmsLikes.entrySet().stream()
                .sorted(Map.Entry.<Film, Integer>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        filmStorage.deleteLike(filmId, userId);
    }
}
