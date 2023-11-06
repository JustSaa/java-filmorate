package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на добавление пользователя");
        User userToAdd = userService.addUser(user);
        log.info("Пользователь успешно добавлен. ID пользователя: {}", userToAdd.getId());
        return userToAdd;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        Collection<User> allUsers = userService.getAllUsers();
        log.info("Получен список пользователей. Текущее количество: {}", allUsers.size());
        return allUsers;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        log.info("Получен запрос на получение пользователя");
        User user = userService.getUser(id);
        log.info("Пользователь с ID: {} получен", id);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя");
        User updatedUser = userService.updateUser(user);
        log.info("Пользователь успешно обновлен. ID пользователя: {}", updatedUser.getId());
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        log.info("Получен запрос на удаление пользователя с ID: {}", id);
        userService.deleteUser(id);
        log.info("Пользователь удален. ID пользователя: {}", id);
    }

    @PutMapping("/{userId}/friends/{userFriendId}")
    public void addToFriend(@PathVariable int userId, @PathVariable int userFriendId) {
        log.info("Получен запрос на добавление друга с :{} к пользователю с ID: {}", userFriendId, userId);
        userService.addToFriend(userId, userFriendId);
        log.info("Пользователь добавлен в друзья");
    }

    @DeleteMapping("/{userId}/friends/{userFriendId}")
    public void deleteFromFriends(@PathVariable int userId, @PathVariable int userFriendId) {
        log.info("Получен запрос на удаление друга с :{} у пользователя с ID: {}", userFriendId, userId);
        userService.deleteFromFriends(userId, userFriendId);
        log.info("Пользователь с ID:{} удален из друзей", userFriendId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int userId, @PathVariable int otherId) {
        log.info("Получен запрос на удаление друга с :{} у пользователя с ID: {}", otherId, userId);
        Collection<User> commonFriends = userService.getCommonFriends(userId, otherId);
        log.info("Количество общих друзьей: {}", commonFriends.size());
        return commonFriends;
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable int userId) {
        log.info("Получен запрос списка друзей для пользователя с ID: {}", userId);
        List<User> userFriends = userService.getFriends(userId);
        log.info("Количество друзей у пользователя с ID: {}", userFriends.size());
        return userFriends;
    }

    @ExceptionHandler
    public ResponseEntity<List<String>> nonFoundHandler(final NotFoundException e) {
        return new ResponseEntity<>(List.of(e.getMessage()), HttpStatus.NOT_FOUND);
    }
}