package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;

@Service
public class UserService {

    final private InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addUser(User user) {
        return inMemoryUserStorage.addUser(user);
    }

    public void deleteUser(int userId) {
        inMemoryUserStorage.deleteUser(userId);
    }

    public Collection<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public User updateUser(User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    public void addToFriend(int userId, int userFriendId) {
        updateFriendList(userId, userFriendId, true);
    }

    public void deleteFromFriends(int userId, int userFriendId) {
        updateFriendList(userId, userFriendId, false);
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
        return inMemoryUserStorage.getUser(idUser);
    }

    private void updateFriendList(int userId, int userFriendId, boolean add) {
        User user = getUser(userId);
        User userFriend = getUser(userFriendId);
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
}
