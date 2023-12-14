package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
public class User {
    private int id;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат адреса электронной почты")
    private String email;
    @NotBlank(message = "Логин не должен быть пустым")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,15}$", message = "Логин должен содержать только буквы, цифры, дефисы и подчеркивания, от 3 до 15 символов")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}