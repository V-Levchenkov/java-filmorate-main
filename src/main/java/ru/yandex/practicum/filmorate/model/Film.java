package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    private Long id;
    @NotNull(message = "name can't be empty")
    @NotBlank(message = "name can't be empty")
    private String name;
    @NotNull(message = "description can't be null")
    @Size(min = 1, max = 200)
    private String description;
    @NotNull(message = "releaseDate can't be null")
    private Date releaseDate;
    @Positive(message = "duration should be positive")
    @Min(1)
    private int duration;
    private MpaRating mpa;
    private List<Genre> genres;

}
