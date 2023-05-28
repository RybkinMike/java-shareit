package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    long id;
    @NotNull(message = "ID ещи для бронирования не может быть пустым")
    long itemId;

    long bookerId;

    @NotNull
    @Future(message = "Бронирования в прошлом не допустимы")
    LocalDateTime start;

    @NotNull
    @Future(message = "Бронирования в прошлом не допустимы")
    LocalDateTime end;
}
