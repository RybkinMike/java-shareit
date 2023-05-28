package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ItemAlreadyExistException;
import ru.practicum.shareit.exception.ValidationException;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User saveUser(User user) throws ItemAlreadyExistException;

    User update(long userId, User user) throws ValidationException;

    User getById(long userId);

    void delete(long userId);
}