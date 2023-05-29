package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {

    ItemRepository repository;

    UserService userService;

    BookingRepository bookingRepository;

    CommentRepository commentRepository;

    ItemMapper itemMapper = new ItemMapper();

    BookingMapper bookingMapper = new BookingMapper();

    @Override
    public Collection<ItemWithBooking> getItems(long userId, int from, int size) {
        validateUserId(userId);
        if (from < 0) {
            throw new ValidationException("from is negative");
        }
        Sort sortByDate = Sort.by(Sort.Direction.ASC, "id");
        int pageIndex = from / size;
        Pageable page = PageRequest.of(pageIndex, size, sortByDate);
        return mapToItemWithBooking(repository.findByUserId(userId, page).toList());
    }

    @Override
    public ItemWithBooking getItemById(long userId, long itemId) {
        Item item = repository.findById(itemId).orElseThrow(() -> new NotFoundException(String.format("Вещь с ID =%d не найден", itemId)));
        BookingDto lastBooking;
        BookingDto nextBooking;
        if (userId == item.getUser().getId()) {
            Booking lastBookingForDto = bookingRepository
                    .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(item.getId(), LocalDateTime.now(), "APPROVED")
                    .orElse(null);
            if (lastBookingForDto == null) {
                lastBooking = null;
            } else {
                lastBooking = bookingMapper.toDto(lastBookingForDto);
            }
            Booking nextBookingForDto = bookingRepository.findFirstByItemIdAndEndAfterAndStatusOrderByStartAsc(item.getId(), LocalDateTime.now(), "APPROVED").orElse(null);
            if (nextBookingForDto != null && lastBookingForDto != null) {
                if (lastBookingForDto.equals(nextBookingForDto)) {
                    nextBookingForDto = null;
                }
            }
            if (nextBookingForDto == null) {
                nextBooking = null;
            } else {
                nextBooking = bookingMapper.toDto(nextBookingForDto);
            }
        } else {
            lastBooking = null;
            nextBooking = null;
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return itemMapper.toEntityWithBooking(item, lastBooking, nextBooking, comments);
    }

    @Override
    public Item getItemById(long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID =%d не найден", itemId)));
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        validateUserId(userId);
        Item item = repository.findById(itemId).get();
        if (itemDto.getName() != null) {
                        item.setName(itemDto.getName());
                    }
                    if (itemDto.getDescription() != null) {
                        item.setDescription(itemDto.getDescription());
                    }
                    if (itemDto.getAvailable() != null) {
                        item.setAvailable(itemDto.getAvailable());
                    }
        repository.save(item);
        return itemMapper.toDto(item);
    }

    @Override
    public Item addNewItem(long userId, ItemDto itemDto) {
        if (itemDto.getAvailable() == null || !itemDto.getAvailable()) {
            throw new ValidationException("Вещь должна быть доступна");
        }
        validateUserId(userId);

        User user = userService.getById(userId);
        Item itemForSave = itemMapper.toEntity(user, itemDto);

        return repository.save(itemForSave);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        validateUserId(userId);
        validateOwner(userId, repository.findById(itemId).get());
        repository.deleteByUserIdAndId(userId, itemId);
    }

    @Override
    public Comment addNewComment(long userId, Comment comment, long itemId) {
        List<Booking> bookings = bookingRepository.getBookingByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(userId, itemId, LocalDateTime.now());
        if (!bookings.isEmpty()) {
            comment.setItem(repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found")));
            comment.setAuthorName(userService.getById(userId).getName());
            comment.setCreated(LocalDateTime.now());
            return commentRepository.save(comment);
        } else throw new ValidationException("Пользователь не брал в аренду вещь");
    }

    @Override
    public List<ItemDto> getItemByQuery(String query, int from, int size) {
        if (from < 0) {
            throw new ValidationException("from is negative");
        }
        Sort sortByDate = Sort.by(Sort.Direction.ASC, "id");
        int pageIndex = from / size;
        Pageable page = PageRequest.of(pageIndex, size, sortByDate);

        if (query.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> userItems = repository.getItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, page);
        for (int i = 0; i < userItems.size(); i++) {
            if (!userItems.get(i).getAvailable()) {
                userItems.remove(i);
            }
        }
        return itemMapper.mapToItemDto(userItems);
    }

    private void validateOwner(long userId, Item item) {
        if (userId != item.getUser().getId()) {
            throw new NotFoundException("Вы не являетесь владельцем.");
        }
    }

    public void validateUserId(long userId) {
        userService.getById(userId);
    }

    public List<ItemWithBooking> mapToItemWithBooking(Iterable<Item> items) {
        List<ItemWithBooking> itemWithBookings = new ArrayList<>();
        for (Item item : items) {
            Booking lastBookingForDto = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(item.getId(), LocalDateTime.now(), "APPROVED").orElse(null);
            BookingDto lastBooking;
            if (lastBookingForDto == null) {
                lastBooking = null;
            } else {
                lastBooking = bookingMapper.toDto(lastBookingForDto);
            }
            Booking nextBookingForDto = bookingRepository.findFirstByItemIdAndEndAfterAndStatusOrderByStartAsc(item.getId(), LocalDateTime.now(), "APPROVED").orElse(null);
            BookingDto nextBooking;
            if (nextBookingForDto == null) {
                nextBooking = null;
            } else {
                nextBooking = bookingMapper.toDto(nextBookingForDto);
            }
            List<Comment> comments = commentRepository.findAllByItemId(item.getId());
            itemWithBookings.add(itemMapper.toEntityWithBooking(item, lastBooking, nextBooking, comments));
        }
        return itemWithBookings;
    }
}