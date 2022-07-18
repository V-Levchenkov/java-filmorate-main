package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class MpaRating {
    private final Long id;
    private final String name;

    public MpaRating(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
