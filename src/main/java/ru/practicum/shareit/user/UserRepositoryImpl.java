package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemAlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final List<User> users = new ArrayList<>();
    private  long counter = 0L;

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User save(User user) throws ItemAlreadyExistException {
        for (User userTemp: users) {
            if (user.getEmail().equals(userTemp.getEmail())) {
                 throw new ItemAlreadyExistException("Пользователь с E-mail: " + user.getEmail() + " уже существует");
            }
        }
        user.setId(getId());
        users.add(user);
        return user;
    }

    @Override
    public User update(User user) throws ItemAlreadyExistException {
        if (user.getEmail() != null) {
            for (User userTemp: users) {
                if (user.getEmail().equals(userTemp.getEmail()) && !user.getId().equals(userTemp.getId())) {
                    throw new ItemAlreadyExistException("Пользователь с E-mail: " + user.getEmail() + " уже существует");
                }
            }
        }
        for (User userTemp : users) {
            if (userTemp.getId().equals(user.getId())) {
                int index = users.indexOf(userTemp);
                if (user.getName() == null) {
                    user.setName(userTemp.getName());
                }
                if (user.getEmail() == null) {
                    user.setEmail(userTemp.getEmail());
                }
                users.set(index, user);
            }
        }
        return user;
    }

    @Override
    public User findById(Long id) {
        for (User user: users) {
            if (user.getId().equals(id)) {
                    return user;
                }
            }
        throw new NotFoundException("Пользователь с ID = " + id + "не найден");
    }

    @Override
    public void delete(long userId) {
        users.remove(findById(userId));
    }

    private long getId() {
        return ++counter;
    }
}
