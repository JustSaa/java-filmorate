package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

public interface UserService {
    User addUser(User user);

    void deleteUser(int userId);

    Collection<User> getAllUsers();

    User updateUser(User user);

    List<User> getCommonFriends(int userId, int userFriendId);

    List<User> getFriends(int userId);

    User getUserById(int userId);

    void deleteFromFriends(int userId, int userFriendId);

    void addToFriend(int userId, int userFriendId);
}
