package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MpaRating> getMpaValues() {
        return mpaStorage.getMpaValues();
    }

    public MpaRating getMpaById(Long id) {
        validateMpaId(id);
        return mpaStorage.getMpaById(id);
    }

    private void validateMpaId(Long id) {
        if (mpaStorage.getMpaById(id) == null) {
            throw new NotFoundException(String.format("Рейтинг MPA c id %s не найден.", id));
        }
    }
}
