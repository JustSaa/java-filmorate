package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int idUser = 0;

    @Override
    public User addUser(User user) {
        User userToAdd = user.toBuilder().id(++idUser).build();
        userToAdd = nameValidator(userToAdd);
        userToAdd.createFriendList();
        users.put(userToAdd.getId(), userToAdd);
        return userToAdd;
    }

    @Override
    public void deleteUser(int userId) {
        userCheckInData(userId);
        users.remove(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User getUser(int userId) {
        userCheckInData(userId);
        return users.get(userId);
    }

    @Override
    public User updateUser(User user) {
        userCheckInData(user.getId());
        User userUpdated = nameValidator(user);
        userUpdated.createFriendList();
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