package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.user.*;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {
    private static UserController userController;
    private static UserRepository userRepository;
    private static UserService userService;
    private static ItemController itemController;
    private static ItemRepository itemRepository;
    private static ItemService itemService;
    User user1;
    User user2;
    ItemDto itemDto1;
    ItemDto itemDto2;

    @BeforeEach
    public void beforeEach() throws ValidationException {
        userRepository = new UserRepositoryImpl();
        userService = new UserServiceImpl(userRepository);
        userController = new UserController(userService);
        user1 = new User("User1", "ya@yandex.ru");
        userController.saveNewUser(user1);
        user2 = new User("User2", "ya@mail.ru");
        userController.saveNewUser(user2);
        itemRepository = new ItemRepositoryImpl();
        itemService = new ItemServiceImpl(itemRepository, userRepository);
        itemController = new ItemController(itemService);
        ItemDto itemDto1 = new ItemDto(1L, "Item1", "Description Item1", 0L, true);
        ItemDto itemDto2 = new ItemDto(2L, "Item2", "Description Item2", 0L, true);
        itemService.addNewItem(user1.getId(), itemDto1);
        itemService.addNewItem(user2.getId(), itemDto2);
    }

    @Test
    void findByUserIdTest() {
        Collection<Item> items = itemService.getItems(1);
        assertNotNull(items, "Контроллер пустой.");
        assertEquals(1, items.size(), "Неверное количество вещей.");
    }

    @Test
    void addAndFindByIdTest() throws ValidationException {
        ItemDto itemDto3 = new ItemDto(3L, "Item3", "Description Item3", 0L, true);
        itemService.addNewItem(user1.getId(), itemDto3);
        ItemDto item = itemService.getItemById(itemDto3.getId());
        assertNotNull(item, "Контроллер пустой.");
        assertEquals(itemDto3, item, "Вещи не соответствуют.");
    }

    @Test
    void updateTest() throws ValidationException {
        ItemDto itemDto3 = new ItemDto(3L, "Item4", "Description Item4", 0L, true);
        itemService.addNewItem(user1.getId(), itemDto3);
        ItemDto updateItemDto3 = new ItemDto(3L, "UPDItem4", "Description UPDItem4", 0L, true);
        itemService.updateItem(user1.getId(), updateItemDto3, itemDto3.getId());
        ItemDto item = itemService.getItemById(itemDto3.getId());
        assertNotNull(item, "Контроллер пустой.");
        assertEquals(item.getName(), "UPDItem4", "Вещи не соответствуют.");
    }

    @Test
    void deleteTest() {
        itemService.deleteItem(1, 1);
        Collection<Item> items = itemService.getItems(1);
        assertNotNull(items, "Контроллер пустой.");
        assertEquals(0, items.size(), "Неверное количество вещей.");
    }
}