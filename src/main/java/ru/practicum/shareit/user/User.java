package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level= AccessLevel.PRIVATE)
public class User {
    Long id;
    @NotNull(message = "email не может быть пустым")
    @Email(message = "email введен не верно")
    String email;
    @NotNull(message = "Имя не может быть пустым")
    @NotBlank(message = "Имя не может быть пустым или содержать только пробелы")
    String name;

    public User(String name, String email) {
        this.email = email;
        this.name = name;
        this.id = -1L;
    }
}
