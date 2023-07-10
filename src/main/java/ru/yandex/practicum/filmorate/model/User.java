package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Data
public class User {
    private final int id;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат адреса электронной почты")
    private final String email;
    @NotNull(message = "Логин не может быть пустым")
    @NotBlank(message = "Логин не может содержать пробелы")
    private final String login;
    private String name;
    @NotNull(message = "Дата рождения не может быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private final LocalDate birthday;
}
