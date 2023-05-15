package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    long id;
    @NotBlank(message = "Имя не может быть пустым или содержать только пробелы")
    String name;
    @NotBlank(message = "Описание не может быть пустым или содержать только пробелы")
    String description;
    long count;
    @AssertTrue
    Boolean available;
}
