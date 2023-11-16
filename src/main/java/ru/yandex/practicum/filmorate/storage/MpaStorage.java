package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public Collection<Mpa> getAllMpa() {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa");
        Collection<Mpa> mpaSQL = new ArrayList<>();
        while (mpaRows.next()) {
            mpaSQL.add(getMpaBD(mpaRows));
        }
        return mpaSQL;
    }

    public Mpa getMpaId(Integer id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE id=?", id);
        mpaRows.next();
        if (mpaRows.last()) {
            return getMpaBD(mpaRows);
        } else {
            return null;
        }
    }

    private Mpa getMpaBD(SqlRowSet mpaRows) {
        Mpa mpaSQL = new Mpa();
        mpaSQL.setId(mpaRows.getInt("id"));
        mpaSQL.setName(mpaRows.getString("rating"));
        return mpaSQL;
    }
}