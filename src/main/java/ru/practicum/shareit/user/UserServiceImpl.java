package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemAlreadyExistException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User saveUser(User user) throws ItemAlreadyExistException {
        return repository.save(user);
    }

    @Override
    public User update(long userId, User user) throws ValidationException {
        user.setId(userId);
        return repository.update(user);
    }

    @Override
    public User getById(long userId) {
        return repository.findById(userId);
    }

    @Override
    public void delete(long userId) {
        repository.delete(userId);
    }


}