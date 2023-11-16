package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storage;

import javax.validation.ValidationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("databaseFilmStorage")
public class FilmDbStorage implements Storage<Film> {

    private final JdbcTemplate jdbcTemplate;
    private int filmId = 0;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) {
        realiseDateValidation(film);
        film.setId(++filmId);
        String sqlQuery = "INSERT INTO films(id, name, description, releaseDate," +
                "duration, mpa_id, rate) VALUES (?,?,?,?,?,?,?)";
        jdbcTemplate.update(sqlQuery, film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getRate());
        String sqlQueryForGetFilm = "SELECT * FROM films f WHERE f.id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQueryForGetFilm, film.getId());
        updateGenreInDB(film);
        return getFilmFromDB(filmRows);
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("releaseDate").toLocalDate();
        int duration = resultSet.getInt("duration");
        Set<Integer> likes = getLikesFromDB(id);
        List<Genre> genres = getGenresFromDB(id);
        Mpa mpa = getMpaFromDB(resultSet.getInt("mpa_id"));
        int rate = resultSet.getInt("rate");

        return new Film(id, name, description, releaseDate, duration, rate, likes, genres, mpa);
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET name=?, description=?, releaseDate=?, duration=?, mpa_id=?, rate=? WHERE id=?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getRate(), film.getId());
        String sqlQueryForGetFilm = "SELECT * FROM films WHERE id=?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQueryForGetFilm, film.getId());
        updateGenreInDB(film);
        return getFilmFromDB(filmRows);
    }

    @Override
    public void delete(int id) {
        String sqlDeleteQuery = "DELETE FROM films WHERE id=?";
        jdbcTemplate.update(sqlDeleteQuery, id);
    }

    @Override
    public Film get(int id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sql, id);
        return getFilmFromDB(filmRow);
    }

    public void realiseDateValidation(Film film) {
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза пуста");
        } else {
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
            }
        }
    }

    private Film getFilmFromDB(SqlRowSet filmRows) {

        if (filmRows.next()) {
            int filmId = filmRows.getInt("id");

            Film film = Film.builder()
                    .id(filmId)
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("releaseDate").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .mpa(getMpaFromDB(filmRows.getInt("mpa_id")))
                    .rate(filmRows.getInt("rate"))
                    .genres(getGenresFromDB(filmId))
                    .likes(getLikesFromDB(filmId))
                    .build();
            return film;
        } else {
            throw new NotFoundException("Фильм не найден в Базе Данных");
        }
    }

    private Mpa getMpaFromDB(int mpaID) {
        String selectSql = "SELECT * FROM mpa WHERE id = ?";
        SqlRowSet mpaSqlRowSet = jdbcTemplate.queryForRowSet(selectSql, mpaID);

        if (mpaSqlRowSet.next()) {
            return new Mpa(mpaID, mpaSqlRowSet.getString("rating"));
        } else {
            throw new NotFoundException("Mpa с id " + mpaID + " не найден в Базе Данных");
        }
    }

    private void updateGenreInDB(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            deleteGenresForFilm(film.getId());
            return;
        }

        Set<Integer> genreIds = new HashSet<>();
        List<Genre> genres = film.getGenres();

        List<Genre> uniqueGenres = genres.stream()
                .filter(genre -> genreIds.add(genre.getId()))
                .collect(Collectors.toList());

        deleteGenresForFilm(film.getId());

        String sqlQueryForGenre = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sqlQueryForGenre, uniqueGenres, uniqueGenres.size(),
                (ps, genre) -> {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, genre.getId());
                });
    }


    private void deleteGenresForFilm(int filmId) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        try {
            jdbcTemplate.update(sqlQuery, filmId);
        } catch (Exception e) {
            log.error("Ошибка удаления жанров для filmId {}", filmId, e);
        }
    }


    private List<Genre> getGenresFromDB(int filmId) {

        String sqlQuery = "SELECT g.id, g.name FROM genre g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
            int genreId = resultSet.getInt("id");
            String genreName = resultSet.getString("name");
            return new Genre(genreId, genreName);
        }, filmId);
    }

    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";

        try {
            jdbcTemplate.update(sqlQuery, filmId, userId);
        } catch (Exception e) {
            log.error("Ошибка добавления лайка filmId {} и userId {}", filmId, userId, e);
        }
    }


    public void deleteLike(int filmId, int userId) {
        if (userId > 0 && userExists(userId)) {
            String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";

            try {
                jdbcTemplate.update(sqlQuery, filmId, userId);
            } catch (Exception e) {
                log.error("Ошибка удаления лайка для filmId {} и userId {}", filmId, userId, e);
            }
        } else {
            log.warn("Некорректное значение userId: {}", userId);
            throw new NotFoundException("Ошибка проверки существования пользователя");
        }
    }

    private boolean userExists(int userId) {
        String sqlQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sqlQuery, Integer.class, userId);
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("Ошибка проверки существования пользователя с userId {}", userId, e);
            return false;
        }
    }

    public Set<Integer> getLikesFromDB(int filmId) {
        String sqlQuery = "SELECT user_id FROM film_likes WHERE film_id = ?";

        try {
            return new HashSet<>(jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId));
        } catch (Exception e) {
            log.error("Ошибка получения likes для filmId {}", filmId, e);
            return new HashSet<>();
        }
    }

}
