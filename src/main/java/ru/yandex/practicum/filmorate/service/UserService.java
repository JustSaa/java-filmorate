package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Storage<User> userStorage;

    @Autowired
    public UserService(@Qualifier("databaseUsersStorage") Storage<User> userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.add(user);
    }

    public void deleteUser(int userId) {
        userStorage.delete(userId);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public List<User> getCommonFriends(int userId, int userFriendId) {
        Map<Integer, Boolean> userFriends = getUser(userId).getFriends();
        Map<Integer, Boolean> userFriendFriendList = getUser(userFriendId).getFriends();

        Set<Integer> userCommonFriendsId = new HashSet<>(userFriends.keySet());

        userCommonFriendsId.retainAll(userFriendFriendList.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet()));

        List<User> userFriendList = new ArrayList<>();

        for (Integer friendUserId : userCommonFriendsId) {
            userFriendList.add(getUser(friendUserId));
        }

        return userFriendList;
    }


    public List<User> getFriends(int userId) {
        List<User> confirmedFriends = new ArrayList<>();
        User user = getUser(userId);

        for (Map.Entry<Integer, Boolean> entry : user.getFriends().entrySet()) {
            if (entry.getValue()) {
                confirmedFriends.add(getUser(entry.getKey()));
            }
        }
        return confirmedFriends;
    }


    public User getUser(int userId) {
        return userStorage.get(userId);
    }

    private void updateFriendList(int userId, int userFriendId, boolean add) {
        User user = getUser(userId);
        User userFriend = getUser(userFriendId);
        if (user != null && userFriend != null) {
            if (add) {
                user.getFriends().put(userFriendId, true);
                friendshipStatusCheck(userFriend, user);
            } else {
                user.getFriends().remove(userFriendId);
                userFriend.getFriends().remove(userId);
            }
        } else {
            throw new NotFoundException("Пользователь или друг не был найден");
        }
    }

    private void friendshipStatusCheck(User user, User userFriend) {
        if (user.getFriends().containsKey(userFriend.getId())) {
            user.getFriends().put(userFriend.getId(), true);
        } else {
            user.getFriends().put(userFriend.getId(), false);
        }
    }

    public void addToFriend(int userId, int userFriendId) {
        if (userStorage instanceof UserDbStorage) {
            ((UserDbStorage) userStorage).addToFriendDB(userId, userFriendId);
        } else {
            updateFriendList(userId, userFriendId, true);
        }
    }

    public void deleteFromFriends(int userId, int userFriendId) {
        if (userStorage instanceof UserDbStorage) {
            ((UserDbStorage) userStorage).deleteFromFriendsDB(userId, userFriendId);
        } else {
            updateFriendList(userId, userFriendId, false);
        }
    }
}
