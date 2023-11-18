package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenresStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenresService {

    private final GenresStorage genreStorage;

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreId(Integer id) {
        Genre genre = genreStorage.getGenreId(id);
        if (genre == null) {
            throw new NotFoundException("genre id not found");
        }
        return genre;
    }
}