package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final Map<Integer, User> users = new HashMap<>();
    private int idUser = 0;

    public User addUser(User user) {
        User userToAdd = user.toBuilder().id(++idUser).build();
        userToAdd = nameValidator(userToAdd);
        users.put(userToAdd.getId(), userToAdd);
        return userToAdd;
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            User userUpdated = nameValidator(user);
            users.put(userUpdated.getId(), userUpdated);
            return userUpdated;
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public User nameValidator(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }
}
