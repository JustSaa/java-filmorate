package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface Storage<T> {
    T add(T entity);

    Collection<T> getAll();

    T update(T entity);

    void delete(int id);

    T get(int id);
}
