package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.*;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private static UserController controller;
    private static UserRepository userRepository;
    private static UserService userService;
    User user1;
    User user2;

    @BeforeEach
    public void beforeEach() throws ValidationException {
        userRepository = new UserRepositoryImpl();
        userService = new UserServiceImpl(userRepository);
        controller = new UserController(userService);
        user1 = new User("User1", "ya@yandex.ru");
        controller.saveNewUser(user1);
        user2 = new User("User2", "ya@mail.ru");
    }

    @Test
    void findAllTest() {
        Collection<User> users = controller.getAllUsers();
        assertNotNull(users, "Контроллер пустой.");
        assertEquals(1, users.size(), "Неверное количество пользователей.");
    }

    @Test
    void create() throws ValidationException {
        Collection<User> users = controller.getAllUsers();
        assertEquals(1, users.size(), "Неверное количество пользователей.");
        controller.saveNewUser(user2);
        Collection<User> users2 = controller.getAllUsers();
        assertEquals(2, users2.size(), "Неверное количество пользователей.");
        assertTrue(users2.contains(user1), "User1 отсутствует.");
        assertTrue(users2.contains(user2), "User2 отсутствует.");
    }

    @Test
    void update() throws ValidationException {
        Collection<User> users = controller.getAllUsers();
        assertEquals(1, users.size(), "Неверное количество пользователей.");
        User userUpdate = new User("Михалыч", "ya@yandex.ru");
        userUpdate.setId(1L);
        controller.update(userUpdate, 1);
        Collection<User> users2 = controller.getAllUsers();
        System.out.println(users2);
        assertEquals(1, users2.size(), "Неверное количество пользователей.");
        assertTrue(users2.contains(userUpdate), "Пользователь1 не обновился.");
    }

    @Test
    void shouldTrowExceptionIfCreateUserThatContains() throws ValidationException {
        User user3 = new User("11111", "ya@yandex.ru");
        Throwable thrown = assertThrows(RuntimeException.class, () -> {
            controller.saveNewUser(user3);
        });
        assertNotNull(thrown.getMessage(), "Сообщение пустое");
        assertEquals("ru.practicum.shareit.exception.ItemAlreadyExistException: Пользователь с E-mail: ya@yandex.ru уже существует", thrown.getMessage(), "Сообщение об ошибке не соответствует");
    }
}
