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
import java.util.HashSet;
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

        Film newFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.now())
                .duration(120)
                .mpa(new Mpa(1, "PG-13"))
                .rate(4)
                .genres(List.of(new Genre(1, "Action"), new Genre(2, "Adventure")))
                .build();

        Film addedFilm = filmDbStorage.add(newFilm);
        Film retrievedFilm = filmDbStorage.get(addedFilm.getId());

        assertThat(retrievedFilm).isNotNull().usingRecursiveComparison().isEqualTo(addedFilm);
    }

    @Test
    void testUpdateFilm() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);

        Film newFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.now())
                .duration(120)
                .mpa(new Mpa(1, "PG-13"))
                .rate(4)
                .likes(new HashSet<>())
                .genres(List.of(new Genre(1, "Action"), new Genre(2, "Adventure")))
                .build();

        Film addedFilm = filmDbStorage.add(newFilm);
        Film updatedFilm = Film.builder()
                .id(addedFilm.getId())
                .name("Updated Film")
                .description("Updated Description")
                .releaseDate(LocalDate.now())
                .duration(150)
                .mpa(new Mpa(2, "PG"))
                .rate(5)
                .likes(new HashSet<>())
                .genres(List.of(new Genre(3, "Мультфильм")))
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

        Film newFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.now())
                .duration(120)
                .mpa(new Mpa(1, "R"))
                .rate(4)
                .genres(List.of(new Genre(1, "Action"), new Genre(2, "Adventure")))
                .likes(new HashSet<>())
                .build();

        Film addedFilm = filmDbStorage.add(newFilm);
        filmDbStorage.delete(addedFilm.getId());

        assertThrows(Exception.class, () -> filmDbStorage.get(addedFilm.getId()));
    }
}
