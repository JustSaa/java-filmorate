package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addToFriend(int userId, int userFriendId) {
        userStorage.addToFriend(userId, userFriendId);
    }

    public void deleteFromFriends(int userId, int userFriendId) {
        userStorage.deleteFromFriends(userId, userFriendId);
    }

    public Set<User> getCommonFriends(int userId, int userFriendId) {
        Set<Integer> user = getUser(userId).getFriends();
        Set<Integer> userFriend = getUser(userFriendId).getFriends();
        Set<Integer> userCommonFriendsId = new HashSet<>(user);
        userCommonFriendsId.retainAll(userFriend);
        Set<User> userFriendList = new HashSet<>();
        for (Integer friendUserId : userCommonFriendsId) {
            userFriendList.add(getUser(friendUserId));
        }
        return userFriendList;
    }

    public List<User> getFriends(int userId) {
        List<User> userFriends = new ArrayList<>();
        User user = getUser(userId);
        for (Integer userFriendId : user.getFriends()) {
            userFriends.add(getUser(userFriendId));
        }
        return userFriends;
    }

    public User getUser(int idUser) {
        return userStorage.getUser(idUser);
    }
}
