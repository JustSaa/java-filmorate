package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testAddUser() {
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(newUser);

        User savedUser = userStorage.getById(1);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testGetAllUsers() {
        User newUser1 = new User(1, "user1@email.ru", "user1", "User One", LocalDate.of(1990, 1, 1));
        User newUser2 = new User(2, "user2@email.ru", "user2", "User Two", LocalDate.of(1995, 2, 2));

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(newUser1);
        userStorage.add(newUser2);

        assertThat(userStorage.getAll())
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    public void testUpdateUser() {
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(newUser);

        User updatedUser = new User(1, "updated@email.ru", "vanya456", "Updated Ivan", LocalDate.of(1995, 5, 5));
        userStorage.update(updatedUser);

        User retrievedUser = userStorage.getById(1);

        assertThat(retrievedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedUser);
    }

    @Test
    public void testDeleteUser() {
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(newUser);

        userStorage.delete(1);

        assertThatThrownBy(() -> userStorage.getById(1))
                .isInstanceOf(NotFoundException.class);
    }
}
