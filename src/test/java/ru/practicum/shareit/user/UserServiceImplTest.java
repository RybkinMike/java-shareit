package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllUsersTest() {
        User expectedUser1 = new User();
        User expectedUser2 = new User();
        List<User> usersList = List.of(expectedUser1, expectedUser2);
        when(userRepository.findAll()).thenReturn(usersList);

        List<User> actualUserList = userService.getAllUsers();

        assertEquals(usersList, actualUserList);
    }

    @Test
    void saveUserTest() {
        User userToSave = new User();
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        User actualUser = userService.saveUser(userToSave);

        assertEquals(userToSave, actualUser);
    }

    @Test
    void updateTest() throws ValidationException {
        User userToUpdate = new User();
        userToUpdate.setId(1L);
        userToUpdate.setName("UserToUpdate");
        userToUpdate.setEmail("email@UPD.ru");
        User userInDB = new User();
        userInDB.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userInDB));
        when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);

        User actualUser = userService.update(userToUpdate.getId(), userToUpdate);

        assertEquals(userToUpdate, actualUser);
        verify(userRepository).findById(1L);
        verify(userRepository).save(userToUpdate);
    }

    @Test
    void updateShouldThrowExceptionTest() throws ValidationException {
        User userToUpdate = new User();
        userToUpdate.setId(1L);
        userToUpdate.setName("UserToUpdate");
        userToUpdate.setEmail("email@UPD.ru");
        when(userRepository.findById(userToUpdate.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getById(userToUpdate.getId()));

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(userToUpdate);
    }

    @Test
    void getByIdTest() {
        long userId = 1L;
        User expectedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getById(userId);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getByIdShouldThrowExceptionTest() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getById(userId));
    }

    @Test
    void deleteTest() {
        User userToDelete = new User();
        userToDelete.setId(1L);

        userService.delete(userToDelete.getId());

        verify(userRepository).deleteById(1L);
    }
}