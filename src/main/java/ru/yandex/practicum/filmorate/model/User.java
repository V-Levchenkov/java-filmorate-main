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
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    private Long id;
    private Set<Long> friends = new HashSet<>();
    @Email(message = "invalid email")
    @NotNull(message = "email can't be empty")
    @NotBlank(message = "email can't be empty")
    private String email;
    @NotBlank(message = "login can't be empty")
    @NotNull(message = "login can't be empty")
    private String login;
    private String name;
    @NotNull(message = "birthday can't be forget")
    @Past(message = "birthday can't be in future")
    private LocalDate birthday;

    public void addFriend(Long friendId) {
        friends.add(friendId);
    }

    public void deleteFriend(Long friendId) {
        friends.remove(friendId);
    }

}
