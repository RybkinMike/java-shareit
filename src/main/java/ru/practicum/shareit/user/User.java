package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "email", nullable = false)
    @NotNull(message = "email не может быть пустым")
    @Email(message = "email введен не верно")
    String email;

    @Column(name = "name", nullable = false)
    @NotNull(message = "Имя не может быть пустым")
    @NotBlank(message = "Имя не может быть пустым или содержать только пробелы")
    String name;
}
