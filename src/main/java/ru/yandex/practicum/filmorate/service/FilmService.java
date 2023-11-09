package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {
    private final Storage<Film> filmStorage;

    @Autowired
    public FilmService(Storage<Film> filmStorage) {
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

    public List<Film> getTop(int count) {
        Collection<Film> allFilms = filmStorage.getAll();
        List<Film> filmList = new ArrayList<>(allFilms);
        filmList.sort((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()));
        int actualCount = Math.min(count, filmList.size());
        return filmList.subList(0, actualCount);
    }

    private void updateLikesList(int filmId, int userId, boolean add) {
        Film film = getFilm(filmId);
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
