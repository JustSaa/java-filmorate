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
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
        //Set<Integer> likes = resultSet.getString("name");
        Set<Integer> likes = null;
        List<Genre> genres = getGenresFromDB(id);
        Mpa mpa = getMpaFromDB(resultSet.getInt("mpa_id"));
        int rate = resultSet.getInt("rate");

        return new Film(id, name, description, releaseDate, duration, rate, likes, genres, mpa);
        //return null;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET name=?, description=?, releaseDate=?, duration=?, mpa_id=?, rate=? WHERE id=?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getRate(), film.getId());
        String sqlQueryForGetFilm = "SELECT * FROM films WHERE id=?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQueryForGetFilm, film.getId());
        return getFilmFromDB(filmRows);
    }

    @Override
    public void delete(int id) {

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
        if (film.getGenres() == null) {
            return;
        }
        List<Genre> genres = film.getGenres();
        String sqlQueryForGenre = "INSERT INTO film_genres(film_id, genre_id) VALUES (?,?)";
        jdbcTemplate.batchUpdate(sqlQueryForGenre, genres, genres.size(),
                (ps, genre) -> {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, genre.getId());
                });
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

}
