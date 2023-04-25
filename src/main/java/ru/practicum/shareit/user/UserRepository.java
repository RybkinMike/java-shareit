package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ItemAlreadyExistException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User save(User user) throws ItemAlreadyExistException;

    User update(User user) throws ValidationException;

    User findById(Long id);

    void delete(long userId);
}
