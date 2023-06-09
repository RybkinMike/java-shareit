package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private final ItemMapper itemMapper = new ItemMapper();


    private final BookingMapper bookingMapper = new BookingMapper();

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getItemsTest() {
        Item item1 = new Item();
        Item item2 = new Item();
        item1.setId(1L);
        item1.setName("Item1");
        item1.setDescription("Des1");
        item2.setId(2L);
        item2.setName("Item2");
        item2.setDescription("Des2");
        long userId = 1L;
        int from = 1;
        int size = 10;
        int pageIndex = from / size;
        Sort sortByDate = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(pageIndex, size, sortByDate);
        List<Item> itemList = List.of(item1, item2);
        Page<Item> itemPage = new PageImpl<>(itemList, page, itemList.size());
        List<Comment> comments = new ArrayList<>();
        ItemWithBooking itemWithBooking1 = itemMapper.toEntityWithBooking(item1, null, null, comments);
        ItemWithBooking itemWithBooking2 = itemMapper.toEntityWithBooking(item2, null, null, comments);
        List<ItemWithBooking> expectItemWithBookingList = List.of(itemWithBooking1, itemWithBooking2);
        when(itemRepository.findByUserId(1L,page)).thenReturn(itemPage);

        List<ItemWithBooking> actualItemWithBookingList = (List<ItemWithBooking>) itemService.getItems(1L, 0, 10);

        assertEquals(expectItemWithBookingList, actualItemWithBookingList);

    }

    @Test
    void getItemsShouldThrowExceptionTest() {
        assertThrows(ValidationException.class,
                () -> itemService.getItems(1L, -1, 10));
    }

    @Test
    void getItemsUserNotValidTest() {

        when(userService.getById(1L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> itemService.getItems(1L, 1, 10));
    }

    @Test
    void getItemByIdIdTest() {
        Item item1 = new Item();
        item1.setId(1L);
        User user = new User();
        user.setId(2L);
        User booker = new User();
        booker.setId(3L);
        item1.setUser(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setItem(item1);
        lastBooking.setBooker(booker);
        lastBooking.setStart(LocalDateTime.of(2022, 01, 01, 01, 01));
        lastBooking.setEnd(LocalDateTime.of(2022, 01, 01, 02, 01));
        Booking nextBooking = new Booking();
        nextBooking.setItem(item1);
        nextBooking.setBooker(booker);
        nextBooking.setStart(LocalDateTime.of(2024, 01, 01, 01, 01));
        nextBooking.setEnd(LocalDateTime.of(2024, 01, 01, 02, 01));
        nextBooking.setId(2L);
        List<Comment> comments = new ArrayList<>();
        ItemWithBooking itemWithBooking = itemMapper.toEntityWithBooking(item1,  bookingMapper.toDto(lastBooking), bookingMapper.toDto(nextBooking), comments);
        when(bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(anyLong(), any(LocalDateTime.class), anyString()))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository
                .findFirstByItemIdAndEndAfterAndStatusOrderByStartAsc(anyLong(), any(LocalDateTime.class), anyString()))
                .thenReturn(Optional.of(nextBooking));

        when(commentRepository.findAllByItemId(1L)).thenReturn(comments);


        ItemWithBooking actualItemWithBooking = itemService.getItemById(2L, 1L);

        assertEquals(itemWithBooking, actualItemWithBooking);
    }

    @Test
    void getItemByIdTest() {
        Item item1 = new Item();
        item1.setId(1L);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        Item actualItem = itemRepository.findById(1L).get();

        assertEquals(item1, actualItem);
    }

    @Test
    void updateItemTest() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Name");
        item1.setDescription("Description");
        item1.setAvailable(true);

        Item itemToUPD = new Item();
        itemToUPD.setId(1L);
        itemToUPD.setName("NameUPD");
        itemToUPD.setDescription("DescriptionUPD");
        itemToUPD.setAvailable(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        ItemDto actualItem = itemService.updateItem(1L, itemMapper.toDto(itemToUPD), 1L);

        assertEquals(itemMapper.toDto(itemToUPD), actualItem);

        verify(itemRepository).save(itemToUPD);
    }

    @Test
    void updateItemTestShouldThrowException() {
        ItemDto item1 = new ItemDto();

        doThrow(NotFoundException.class).when(userService).getById(anyLong());


        assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, item1, 1L));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void addNewItemTest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(true);
        User user = new User();
        user.setId(1L);
        Item item = itemMapper.toEntity(user, itemDto);
        when(userService.getById(1L)).thenReturn(user);
        when(itemRepository.save(item)).thenReturn(item);

        Item actualItem = itemService.addNewItem(1L, itemDto);

        assertEquals(item, actualItem);
    }

    @Test
    void addNewItemTestShouldThrowException() {
        ItemDto itemDto = new ItemDto();
        User user = new User();
        user.setId(1L);
        Item item = itemMapper.toEntity(user, itemDto);

        assertThrows(ValidationException.class,
                () -> itemService.addNewItem(1L, itemDto));
        verify(itemRepository, never()).save(any(Item.class));

    }

    @Test
    void deleteItemTest() {
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setUser(owner);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        itemService.deleteItem(1L, 1L);
        verify(itemRepository).deleteByUserIdAndId(anyLong(), anyLong());
    }

    @Test
    void deleteItemTestShouldThrowException() {
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.toString();
        item.setId(1L);
        item.setUser(owner);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,
                () -> itemService.deleteItem(2L, 1L));

        verify(itemRepository, never()).deleteByUserIdAndId(anyLong(), anyLong());
    }

    @Test
    void addNewCommentTest() {
        Comment comment = new Comment();
        comment.toString();
        User user = new User();
        user.setName("USER");
        Item item = new Item();
        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        bookings.add(booking);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthorName("USER");
        when(bookingRepository.getBookingByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(bookings);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userService.getById(1L)).thenReturn(user);
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment actualComment = itemService.addNewComment(1L, comment, 1L);

        assertEquals(comment, actualComment);
    }

    @Test
    void addNewCommentTestShouldThrowValidationException() {
        Comment comment = new Comment();
        User user = new User();
        user.setName("USER");
        Item item = new Item();
        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        bookings.add(booking);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthorName("USER");

        assertThrows(ValidationException.class,
                () -> itemService.addNewComment(1L, comment, 1L));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addNewCommentTestShouldThrowNotFoundException() {
        Comment comment = new Comment();
        User user = new User();
        user.setName("USER");
        Item item = new Item();
        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        bookings.add(booking);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthorName("USER");
        when(bookingRepository.getBookingByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(bookings);

        assertThrows(NotFoundException.class,
                () -> itemService.addNewComment(1L, comment, 1L));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getItemByQueryTest() {
        Item item = new Item();
        item.setAvailable(true);
        item.setName("Покойник");
        String query = "покой";
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemDto> itemsDto = new ArrayList<>();
        itemsDto.add(itemMapper.toDto(item));
        when(itemRepository.getItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(anyString(), anyString(), any(Pageable.class))).thenReturn(items);

        List<ItemDto> actualItems = itemService.getItemByQuery(query, 1, 5);

        assertEquals(itemsDto, actualItems);
    }

    @Test
    void getItemByQueryTestShouldThrowException() {
        Item item = new Item();
        item.setAvailable(true);
        item.setName("Покойник");
        String query = "покой";
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemDto> itemsDto = new ArrayList<>();
        itemsDto.add(itemMapper.toDto(item));

        assertThrows(ValidationException.class,
                () -> itemService.getItemByQuery(query, -1, 5));
    }

    @Test
    void mapToItemWithBooking() {
        Item item = new Item();
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemWithBooking> itemsWithBooking = new ArrayList<>();
        ItemWithBooking itemWithBooking = itemMapper.toEntityWithBooking(item, null, null, null);
        itemWithBooking.toString();
        itemWithBooking.hashCode();
        List<Comment> comments = new ArrayList<>();
        itemWithBooking.setComments(comments);
        itemsWithBooking.add(itemWithBooking);

        List<ItemWithBooking> actualItems = itemService.mapToItemWithBooking(items);

        assertEquals(itemsWithBooking, actualItems);
    }
}