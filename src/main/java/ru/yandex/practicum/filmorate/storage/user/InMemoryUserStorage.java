package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class InMemoryUserStorage implements Storage<User> {

    private final Map<Integer, User> users = new HashMap<>();
    private int idUser = 0;

    @Override
    public User add(User user) {
        User userToAdd = user.toBuilder().id(++idUser).build();
        userToAdd = nameValidator(userToAdd);
        users.put(userToAdd.getId(), userToAdd);
        return userToAdd;
    }

    @Override
    public void delete(int userId) {
        userCheckInData(userId);
        users.remove(userId);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    public User getById(int userId) {
        userCheckInData(userId);
        return users.get(userId);
    }

    @Override
    public User update(User user) {
        userCheckInData(user.getId());
        User userUpdated = nameValidator(user);
        users.put(userUpdated.getId(), userUpdated);
        return userUpdated;
    }

    public User nameValidator(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    private void userCheckInData(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с " + userId + " не найден");
        }
    }
}
