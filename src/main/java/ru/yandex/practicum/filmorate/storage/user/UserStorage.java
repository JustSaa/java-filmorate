package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User addUser(User user);

    void deleteUser(int idUser);

    Collection<User> getAllUsers();

    User updateUser(User user);
}
