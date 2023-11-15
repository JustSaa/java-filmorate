package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("databaseUsersStorage")
public class UserDbStorage implements Storage<User> {
    private final JdbcTemplate jdbcTemplate;
    private int idUser = 0;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        additionUser(user);
        String sqlQuery = "INSERT INTO users(id, email, login, name, birthdate) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sqlQuery, user.getId(), user.getEmail(),
                user.getLogin(), user.getName(), user.getBirthday());
        String sqlQueryForGetUser = "SELECT * FROM users WHERE id=?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQueryForGetUser, user.getId());
        return getUserFromDB(userRows);
    }

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String email = resultSet.getString("email");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name");
        LocalDate birthday = resultSet.getDate("birthdate").toLocalDate();
        Map<Integer, Boolean> friendsList = getFriendListFromDB(id);

        return new User(id, email, login, name, birthday, friendsList);
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users SET email=?, login=?, name=?, birthdate=? WHERE id=?";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getId());
        String sqlQueryForGetUser = "SELECT * FROM users WHERE id=?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQueryForGetUser, user.getId());
        return getUserFromDB(userRows);
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "DELETE FROM users WHERE id=?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public User get(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        return getUserFromDB(userRows);
    }

    private User getUserFromDB(SqlRowSet userRows) {
        if (userRows.next()) {
            int userId = userRows.getInt("id");
            User user = User.builder()
                    .id(userId)
                    .email(userRows.getString("email"))
                    .login(userRows.getString("login"))
                    .name(userRows.getString("name"))
                    .birthday(userRows.getDate("birthdate").toLocalDate())
                    .build();
            user.setFriends(getFriendListFromDB(userId));
            return user;
        } else {
            throw new NotFoundException("Пользователь не найден в Базе Данных");
        }
    }

    private Map<Integer, Boolean> getFriendListFromDB(int userId) {
        Map<Integer, Boolean> friendList = new HashMap<>();
        SqlRowSet userFriendsRows = jdbcTemplate.queryForRowSet("SELECT * FROM user_friends WHERE user_id = ?", userId);
        while (userFriendsRows.next()) {
            int friendId = userFriendsRows.getInt("friend_id");
            boolean status = userFriendsRows.getBoolean("status");
            friendList.put(friendId, status);
        }
        return friendList;
    }

    private void additionUser(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        if (user.getId() == 0) {
            user.setId(++idUser);
        }
    }

    public void addToFriendDB(int id, int friendId) {
        checkUsersInDB(id, friendId);
        String sqlSelect = "SELECT user_id FROM user_friends WHERE user_id=? AND friend_id=?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlSelect, friendId, id);
        SqlRowSet userFriendIdRows = jdbcTemplate.queryForRowSet(sqlSelect, id, friendId);
        if (!userRows.next() && !userFriendIdRows.next()) {
            String sqlInsert = "INSERT INTO user_friends(user_id,friend_id,status) VALUES (?,?,?)";
            jdbcTemplate.update(sqlInsert, id, friendId, true);
            jdbcTemplate.update(sqlInsert, friendId, id, false);
        } else if (userFriendIdRows.next()) {
            String sqlUpdate = "UPDATE user_friends SET status=true WHERE user_id=? AND friend_id=?";
            jdbcTemplate.update(sqlUpdate, friendId, id);
        }
    }

    public void deleteFromFriendsDB(int userId, int userFriendId) {
        String deleteSql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id=?";
        jdbcTemplate.update(deleteSql, userId, userFriendId);
    }

    private void checkUsersInDB(int userId, int userFriendId) {
        String sqlUser = "SELECT * FROM users WHERE id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlUser, userId);
        SqlRowSet userFriendRows = jdbcTemplate.queryForRowSet(sqlUser, userFriendId);
        if (!userRows.next()) {
            throw new NotFoundException("Пользователь c ID:" + userId + "не найден в Базе Данных");
        } else if (!userFriendRows.next()) {
            throw new NotFoundException("Пользователь c ID:" + userId + "не найден в Базе Данных");
        }
    }
}
