package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "Имя фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
    private int rate;
    private Set<Integer> likes;
    private List<Genre> genres;
    private Mpa mpa;

    public void checkLikesList() {
        if (likes == null) {
            likes = new HashSet<>();
        }
    }
}
