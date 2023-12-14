package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserDbStorage userDbStorage;

    @Autowired
    public UserServiceImpl(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public User addUser(User user) {
        return userDbStorage.add(user);
    }

    public void deleteUser(int userId) {
        userDbStorage.delete(userId);
    }

    public Collection<User> getAllUsers() {
        return userDbStorage.getAll();
    }

    public User updateUser(User user) {
        return userDbStorage.update(user);
    }

    public List<User> getCommonFriends(int userId, int userFriendId) {
        Map<Integer, Boolean> userFriends = userDbStorage.getFriendListFromDB(userId);
        Map<Integer, Boolean> userFriendFriendList = userDbStorage.getFriendListFromDB(userFriendId);

        Set<Integer> userCommonFriendsId = new HashSet<>(userFriends.keySet());

        userCommonFriendsId.retainAll(userFriendFriendList.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet()));

        List<User> userFriendList = new ArrayList<>();

        for (Integer friendUserId : userCommonFriendsId) {
            userFriendList.add(getUserById(friendUserId));
        }

        return userFriendList;
    }


    public List<User> getFriends(int userId) {
        List<User> confirmedFriends = new ArrayList<>();

        for (Map.Entry<Integer, Boolean> entry : userDbStorage.getFriendListFromDB(userId).entrySet()) {
            if (entry.getValue()) {
                confirmedFriends.add(getUserById(entry.getKey()));
            }
        }
        return confirmedFriends;
    }

    public User getUserById(int userId) {
        return userDbStorage.getById(userId);
    }

    public void deleteFromFriends(int userId, int userFriendId) {
        userDbStorage.deleteFromFriendsDB(userId, userFriendId);
    }

    public void addToFriend(int userId, int userFriendId) {
        userDbStorage.addToFriendDB(userId, userFriendId);
    }
}
