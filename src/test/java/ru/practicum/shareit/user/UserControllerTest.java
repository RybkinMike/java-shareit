package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidationException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    @Test
    void getAllUsersTest() {
        List<User> users = List.of(new User());
        when(userService.getAllUsers()).thenReturn(users);

        List<User> response = userController.getAllUsers();

        assertEquals(users, response);
    }

    @Test
    void saveNewUserTest() {
        User user = new User();
        when(userService.saveUser(user)).thenReturn(user);

        User response = userController.saveNewUser(user);

        assertEquals(user, response);
    }

    @Test
    void updateTest() {
        User user = new User();
        user.setId(1L);
        when(userService.update(1L, user)).thenReturn(user);
        User response = userController.update(user, 1L);

        assertEquals(user, response);
    }

    @Test
    void getByIdTest() {
        User user = new User();
        user.setId(1L);
        when(userService.getById(1L)).thenReturn(user);
        User response = userController.getById(1L);

        assertEquals(user, response);
    }

    @Test
    void deleteTest() {
        userController.delete(1L);

        verify(userService).delete(anyLong());
    }
}