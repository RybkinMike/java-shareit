package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ItemAlreadyExistException;
import ru.practicum.shareit.exception.ValidationException;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User saveNewUser(@RequestBody @Valid User user) throws ValidationException {
        try {
            return userService.saveUser(user);
        } catch (ItemAlreadyExistException e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/{userId}")
    public User update(@RequestBody User user, @PathVariable long userId) throws ValidationException {
        log.info("Запрос на обновление данных пользователя {}", user);
        return userService.update(userId, user);
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable long userId) {
        return userService.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userService.delete(userId);
    }
}