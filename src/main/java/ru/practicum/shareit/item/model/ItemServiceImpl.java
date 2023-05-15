package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {

    ItemRepository repository;

    UserRepository userRepository;

    BookingRepository bookingRepository;

    CommentRepository commentRepository;

    ItemMapper itemMapper = new ItemMapper();

    BookingMapper bookingMapper = new BookingMapper();

    @Override
    public Collection<ItemWithBooking> getItems(long userId) {
        validateUserId(userId);
        return mapToItemWithBooking(repository.findByUserId(userId));
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

    @Transactional
    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
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

    @Transactional
    @Override
    public Item addNewItem(long userId, ItemDto itemDto) throws ValidationException {

        if (itemDto.getAvailable() == null || !itemDto.getAvailable()) {
            throw new ValidationException("Вещь должна быть доступна");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemMapper.toEntity(user, itemDto);
        validateUserId(userId);
        return repository.save(item);
    }

    @Transactional
    @Override
    public void deleteItem(long userId, long itemId) {
        validateUserId(userId);
        validateOwner(userId, repository.findById(itemId).get());
        repository.deleteByUserIdAndId(userId, itemId);
    }

    @Override
    public Comment addNewComment(long userId, Comment comment, long itemId) throws ValidationException {
        List<Booking> bookings = bookingRepository.getBookingByUserIdAndFinishAfterNow(userId);
        boolean userIsBooker = false;
        for (Booking booking: bookings) {
            if (booking.getItem().getId() == itemId) {
                userIsBooker = true;
            }
        }
        if (userIsBooker) {
            comment.setItem(repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found")));
            comment.setAuthorName(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")).getName());
            comment.setCreated(LocalDateTime.now());
            return commentRepository.save(comment);
        } else throw new ValidationException("Пользователь не брал в аренду вещь");
    }

    @Override
    public List<ItemDto> getItemByQuery(String query) {
        if (query.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> userItems = repository.getItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        for (int i = 0; i < userItems.size(); i++) {
            if (!userItems.get(i).getAvailable()) {
                userItems.remove(i);
            }
        }
        return itemMapper.mapToItemDto(userItems);
    }

    private void validateOwner(long userId, Item item) {
        if (userId != item.getUser().getId()) {
            log.warn("Неправильный ID");
            throw new NotFoundException("Вы не являетесь владельцем.");
        }
    }

    private void validateUserId(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID =%d не найден", userId)));
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
