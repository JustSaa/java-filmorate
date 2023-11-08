package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

public interface FilmStorage extends Storage<Film> {

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);
}