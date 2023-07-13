package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Data
public class User {
    private int id;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат адреса электронной почты")
    private String email;
    @NotBlank(message = "Логин не может содержать пробелы")
    private String login;
    //@NotBlank(message = "Имя не может быть пустым")
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
