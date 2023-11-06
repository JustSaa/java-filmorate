package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class FilmService {
    final private InMemoryFilmStorage inMemoryFilmStorage;
    final private InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Film addFilm(Film film) {
        return inMemoryFilmStorage.addFilm(film);
    }

    public Film getFilm(int filmId) {
        checkFilmInData(filmId);
        return inMemoryFilmStorage.getFilms().get(filmId);
    }

    public Collection<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    public Film updateFilm(Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    public void deleteFilm(int filmId) {
        inMemoryFilmStorage.deleteFilm(filmId);
    }

    private void updateLikesList(int filmId, int userId, boolean add) {
        User user = inMemoryUserStorage.getUsers().get(userId);
        Film film = inMemoryFilmStorage.getFilms().get(filmId);
        if (user != null && film != null) {
            if (add) {
                film.getLikes().add(userId);
            } else {
                film.getLikes().remove(userId);
            }
        } else {
            throw new NotFoundException("Пользователь или фильм с таким ID не был найден");
        }
    }

    public void addLike(int filmId, int userId) {
        updateLikesList(filmId, userId, true);
    }

    public void deleteLike(int filmId, int userId) {
        updateLikesList(filmId, userId, false);
    }

    public List<Film> getTop(int count) {
        Collection<Film> allFilms = inMemoryFilmStorage.getAllFilms();
        List<Film> filmList = new ArrayList<>(allFilms);
        filmList.sort((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()));
        int actualCount = Math.min(count, filmList.size());
        return filmList.subList(0, actualCount);
    }

    public void checkFilmInData(int filmId) {
        if (!inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            throw new NotFoundException("Фильм с ID: " + filmId + "не найден");
        }
    }
}
