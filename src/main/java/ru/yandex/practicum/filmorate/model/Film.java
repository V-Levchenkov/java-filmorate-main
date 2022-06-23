package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Film {
    private Set<Long> likes = new HashSet<>();
    private Long id;
    @NotNull(message = "name can't be empty")
    @NotBlank(message = "name can't be empty")
    private String name;
    @NotNull(message = "description can't be null")
    @Size(min = 1, max = 200)
    private String description;
    @NotNull(message = "releaseDate can't be null")
    private LocalDate releaseDate;
    @Positive(message = "duration should be positive")
    @Min(1)
    private Long duration;

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void removeLike(Long userId) {
        likes.remove(userId);
    }
}
