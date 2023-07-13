package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

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

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя");
        User updatedUser = userService.updateUser(user);
        log.info("Пользователь успешно обновлен. ID пользователя: {}", updatedUser.getId());
        return updatedUser;
    }
}
