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
    public User add(User user) {
        User userToAdd = user.toBuilder().id(++idUser).build();
        userToAdd = nameValidator(userToAdd);
        userToAdd.createFriendList();
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

    public User get(int userId) {
        userCheckInData(userId);
        return users.get(userId);
    }

    @Override
    public User update(User user) {
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

    private void updateFriendList(int userId, int userFriendId, boolean add) {
        User user = get(userId);
        User userFriend = get(userFriendId);
        if (user != null && userFriend != null) {
            if (add) {
                user.getFriends().add(userFriendId);
                userFriend.getFriends().add(userId);
            } else {
                user.getFriends().remove(userFriendId);
                userFriend.getFriends().remove(userId);
            }
        } else {
            throw new NotFoundException("Пользователь или друг не был найден");
        }
    }

    public void addToFriend(int userId, int userFriendId) {
        updateFriendList(userId, userFriendId, true);
    }

    public void deleteFromFriends(int userId, int userFriendId) {
        updateFriendList(userId, userFriendId, false);
    }
}
