package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
class FilmDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testAddAndGetFilm() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Genre actionGenre = new Genre(1, "Action");
        Genre adventureGenre = new Genre(2, "Adventure");

        Film newFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.now())
                .duration(120)
                .mpa(new Mpa(1, "PG-13"))
                .rate(4)
                .genres(new LinkedHashSet<>(Arrays.asList(actionGenre, adventureGenre)))
                .build();

        Film addedFilm = filmDbStorage.add(newFilm);
        Film retrievedFilm = filmDbStorage.getById(addedFilm.getId());

        assertThat(retrievedFilm).isNotNull().usingRecursiveComparison().isEqualTo(addedFilm);
    }

    @Test
    void testUpdateFilm() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Genre actionGenre = new Genre(1, "Action");
        Genre adventureGenre = new Genre(2, "Adventure");

        Film newFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.now())
                .duration(120)
                .mpa(new Mpa(1, "PG-13"))
                .rate(4)
                .genres(new LinkedHashSet<>(Arrays.asList(actionGenre, adventureGenre)))
                .build();

        Film addedFilm = filmDbStorage.add(newFilm);
        Genre cartoonGenre = new Genre(2, "Драма");
        Film updatedFilm = Film.builder()
                .id(addedFilm.getId())
                .name("Updated Film")
                .description("Updated Description")
                .releaseDate(LocalDate.now())
                .duration(150)
                .mpa(new Mpa(2, "PG"))
                .rate(5)
                .genres(new LinkedHashSet<>(List.of(cartoonGenre)))
                .build();

        Film resultFilm = filmDbStorage.update(updatedFilm);

        assertThat(resultFilm).isNotNull().usingRecursiveComparison().isEqualTo(updatedFilm);
    }

    @Test
    void testGetAllFilms() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);

        List<Film> films = (List<Film>) filmDbStorage.getAll();

        assertThat(films).isNotNull().isEmpty();
    }

    @Test
    void testDeleteFilm() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Genre actionGenre = new Genre(1, "Action");
        Genre adventureGenre = new Genre(2, "Adventure");

        Film newFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.now())
                .duration(120)
                .mpa(new Mpa(1, "R"))
                .rate(4)
                .genres(new LinkedHashSet<>(Arrays.asList(actionGenre, adventureGenre)))
                .build();

        Film addedFilm = filmDbStorage.add(newFilm);
        filmDbStorage.delete(addedFilm.getId());

        assertThrows(Exception.class, () -> filmDbStorage.getById(addedFilm.getId()));
    }
}
