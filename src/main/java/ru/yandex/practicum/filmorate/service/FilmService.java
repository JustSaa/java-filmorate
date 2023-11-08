package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film getFilm(int filmId) {
        return filmStorage.get(filmId);
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

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTop(int count) {
        Collection<Film> allFilms = filmStorage.getAll();
        List<Film> filmList = new ArrayList<>(allFilms);
        filmList.sort((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()));
        int actualCount = Math.min(count, filmList.size());
        return filmList.subList(0, actualCount);
    }
}
