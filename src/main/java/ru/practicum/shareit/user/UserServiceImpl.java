package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemAlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Transactional
    @Override
    public User saveUser(User user) throws ItemAlreadyExistException {
        return repository.save(user);
    }

    @Transactional
    @Override
    public User update(long userId, User user) throws ValidationException {
        user.setId(userId);
        Optional<User> userTemp = repository.findById(userId);
                    if (user.getName() == null) {
                    user.setName(userTemp.get().getName());
                }
                if (user.getEmail() == null) {
                    user.setEmail(userTemp.get().getEmail());
                }
        return repository.save(user);
    }

    @Override
    public User getById(long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID =%d не найден", userId)));
    }

    @Transactional
    @Override
    public void delete(long userId) {
        repository.deleteById(userId);
    }
}