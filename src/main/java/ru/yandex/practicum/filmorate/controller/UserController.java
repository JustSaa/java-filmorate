package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idUser = 0;

    @PostMapping
    public User addUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                log.error("Ошибка валидации поля '{}': {}", error.getField(), error.getDefaultMessage());
            }
            throw new ValidationException("Ошибка валидации данных пользователя");
        }
        log.info("Получен запрос на добавление пользователя");
        User userToAdd = user.toBuilder().id(++idUser).build();
        if (userToAdd.getName() == null || userToAdd.getName().isBlank()) {
            userToAdd.setName(userToAdd.getLogin());
        }
        users.put(userToAdd.getId(), userToAdd);
        return userToAdd;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return users.values();
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        log.info("Получен запрос на обновление пользователя");
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                log.error("Ошибка валидации поля '{}': {}", error.getField(), error.getDefaultMessage());
            }
            throw new ValidationException("Ошибка валидации данных пользователя");
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
