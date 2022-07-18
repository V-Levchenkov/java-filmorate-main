package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaStorage {

    MpaRating getMpaById(Long id);

    List<MpaRating> getMpaValues();
}

