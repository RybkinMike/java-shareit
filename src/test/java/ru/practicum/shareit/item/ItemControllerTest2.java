package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ItemControllerTest2 {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemRequestService itemRequestService;

    private final ItemMapper itemMapper = new ItemMapper();

    @SneakyThrows
    @Test
    void getTest() {
        ItemWithBooking itemWithBooking = new ItemWithBooking();
        List<ItemWithBooking> itemWithBookings = List.of(itemWithBooking);
        when(itemService.getItems(1L, 1, 10)).thenReturn(itemWithBookings);

        String response = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "10"))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemWithBookings), response);
    }

    @SneakyThrows
    @Test
    void getByIdTest() {
        ItemWithBooking itemWithBooking = new ItemWithBooking();
        when(itemService.getItemById(1L, 1L)).thenReturn(itemWithBooking);

        String response = mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemWithBooking), response);
    }

    @SneakyThrows
    @Test
    void testGetByIdTest() {
        ItemDto itemDto = new ItemDto();
        List<ItemDto> itemDtos = List.of(itemDto);
        when(itemService.getItemByQuery("good", 1, 10)).thenReturn(itemDtos);

        String response = mockMvc.perform(get("/items//search", 1L)
                        .param("text", "good")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDtos), response);
    }

    @SneakyThrows
    @Test
    void addTest() {
        Item item = new Item();
        item.setName("i");
        item.setDescription("I");
        item.setAvailable(true);
        when(itemService.addNewItem(1L, itemMapper.toDto(item))).thenReturn(item);

        String response = mockMvc.perform(post("/items", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemMapper.toDto(item))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(item), response);
    }

    @SneakyThrows
    @Test
    void addTestShouldThrowExceptionName() {
        Item item = new Item();
        item.setName(null);
        item.setDescription("I");
        item.setAvailable(true);
        when(itemService.addNewItem(1L, itemMapper.toDto(item))).thenReturn(item);

        mockMvc.perform(post("/items", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemMapper.toDto(item))))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).addNewItem(anyLong(), any(ItemDto.class));

    }

    @SneakyThrows
    @Test
    void addTestShouldThrowExceptionDescription() {
        Item item = new Item();
        item.setName("i");
        item.setDescription(null);
        item.setAvailable(true);
        when(itemService.addNewItem(1L, itemMapper.toDto(item))).thenReturn(item);

        mockMvc.perform(post("/items", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemMapper.toDto(item))))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).addNewItem(anyLong(), any(ItemDto.class));

    }

    @SneakyThrows
    @Test
    void addTestShouldThrowExceptionAvailable() {
        Item item = new Item();
        item.setName("i");
        item.setDescription("I");
        item.setAvailable(false);
        when(itemService.addNewItem(1L, itemMapper.toDto(item))).thenReturn(item);

        mockMvc.perform(post("/items", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemMapper.toDto(item))))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).addNewItem(anyLong(), any(ItemDto.class));

    }

    @SneakyThrows
    @Test
    void addCommentTest() {
        Comment comment = new Comment();
        comment.setText("text");
        when(itemService.addNewComment(1L, comment, 1L)).thenReturn(comment);

        String response = mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(comment), response);
    }

    @SneakyThrows
    @Test
    void addCommentTestShouldThrowException() {
        Comment comment = new Comment();
        comment.setText(null);
        when(itemService.addNewComment(1L, comment, 1L)).thenReturn(comment);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).addNewComment(anyLong(), any(Comment.class), anyLong());
    }

    @SneakyThrows
    @Test
    void updateTest() {
        ItemDto itemDto = new ItemDto();
        when(itemService.updateItem(1L, itemDto, 1L)).thenReturn(itemDto);

        String response = mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), response);
    }

    @SneakyThrows
    @Test
    void deleteItemTest() {
        mockMvc.perform(delete("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemService).deleteItem(1L, 1L);
    }
}