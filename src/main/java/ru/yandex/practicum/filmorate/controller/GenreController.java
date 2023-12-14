package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenresService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping(
        value = "/genres",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class GenreController {

    private final GenresService genresService;

    @GetMapping
    public List<Genre> getAllGenres() {
        return genresService.getAllGenres();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Genre getGenreId(@PathVariable("id") @NotNull Integer id) {
        return genresService.getGenreId(id);
    }
}