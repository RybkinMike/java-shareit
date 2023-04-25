package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class User {
    private Long id;

    @NotNull(message = "email введен не верно")
    @Email(message = "email введен не верно")
    private String email;
    @NotNull(message = "Имя не может быть пустым")
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    public User(String name, String email) {
        this.email = email;
        this.name = name;
        this.id = -1L;
    }
}
