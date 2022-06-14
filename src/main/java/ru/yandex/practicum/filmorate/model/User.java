package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    private Long id;
    @Email(message = "invalid email")
    @NotNull(message = "email can't be empty")
    @NotBlank(message = "email can't be empty")
    private String email;
    @NotBlank(message = "login can't be empty")
    @NotNull(message = "login can't be empty")
    private String login;
    private String name;
    @Past(message = "birthday can't be in future")
    private LocalDate birthday;
}
