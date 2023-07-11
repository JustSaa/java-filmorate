package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;


import java.time.LocalDate;

@Builder(toBuilder = true)
@Data
public class Film {
    private final int id;
    @NotBlank(message = "Адрес электронной почты не может быть пустым")
    private final String name;
    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private final String description;
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private final int duration;
}
