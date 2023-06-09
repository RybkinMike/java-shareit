package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {
    ItemService itemService;

    UserService userService;

    BookingRepository repository;

    BookingMapper bookingMapper = new BookingMapper();

    @Override
    public Booking getBookingById(long userId, long bookingId) {
        userService.getById(userId);
        Booking  booking = repository.findById(bookingId).orElseThrow(() -> new NotFoundException(String.format("Бронь с ID =%d не найден", bookingId)));
        User user = booking.getBooker();
        User owner = booking.getItem().getUser();
        if (userId == user.getId() || userId == owner.getId()) {
            return booking;
        }
        throw new NotFoundException(String.format("Пользователь с ID =%d не является ни создателем брони, ни владельцем вещи", userId));
    }

    @Override
    public List<Booking> getByUserId(long userId, String state, int from, int size) {
        userService.getById(userId);
        if (from < 0) {
            throw new ValidationException("from is negative");
        }
        Sort sortByDate = Sort.by(Sort.Direction.ASC, "start");
        int pageIndex = from / size;
        Pageable page = PageRequest.of(pageIndex, size, sortByDate);
        switch (state) {
            case "ALL":
                return repository.getByBookerIdOrderByStartDesc(userId, page).toList();
            case "CURRENT":
                return repository.getCurrentByUserId(userId, page).toList();
            case "PAST":
                return repository.getBookingByUserIdAndFinishAfterNow(userId, page).toList();
            case "FUTURE":
                return repository.getBookingByUserIdAndStarBeforeNow(userId, page).toList();
            case "WAITING":
            case "REJECTED":
                return repository.getByBookerIdAndStatusContainingIgnoreCaseOrderByStartDesc(userId, state, page).toList();
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<Booking> getByOwnerId(long userId, String state, int from, int size) {
        userService.getById(userId);
        if (from < 0) {
            throw new ValidationException("from is negative");
        }
        Sort sortByDate = Sort.by(Sort.Direction.ASC, "start");
        int pageIndex = from / size;
        Pageable page = PageRequest.of(pageIndex, size, sortByDate);
        switch (state) {
            case "ALL":
                return repository.findByOwnerId(userId, page).toList();
            case "CURRENT":
                return repository.getCurrentByOwnerId(userId, page).toList();
            case "PAST":
                return repository.getPastByOwnerId(userId, page).toList();
            case "FUTURE":
                return repository.getBookingByOwnerIdAndStarBeforeNowOrderByStartDesc(userId, page).toList();
            case "WAITING":
            case "REJECTED":
                return repository.getBookingByOwnerIdAndByStatusContainingIgnoreCase(userId, state, page).toList();
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Transactional
    @Override
    public Booking approveBooking(long userId, long bookingId, boolean approve) {
        userService.getById(userId);
        Booking  booking = repository.findById(bookingId).orElseThrow(() -> new NotFoundException(String.format("Бронь с ID =%d не найден", bookingId)));
        if (booking.getStatus().equals("APPROVED")) {
            throw new ValidationException(String.format("Бронь с ID =%d уже подтверждена", bookingId));
        }
        User owner = booking.getItem().getUser();
        if (userId != (owner.getId())) {
            throw new NotFoundException(String.format("Пользователь с ID =%d не является владельцем вещи", userId));
        }
        if (approve) {
            booking.setStatus("APPROVED");
        } else {
            booking.setStatus("REJECTED");
        }
        repository.save(booking);
        return booking;
    }

    @Transactional
    @Override
    public Booking addNewBooking(long userId, BookingDto bookingDto) {
        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new ValidationException("Окончание бронирования должно быть после его начала");
        }
        User user = userService.getById(userId);
        Item item = itemService.getItemById(bookingDto.getItemId());
        if (userId == item.getUser().getId()) {
            throw  new NotFoundException(String.format("Пользователь с ID =%d является владельцем вещи", userId));
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования");
        }
        Booking booking = bookingMapper.toEntity(user, item, bookingDto);
        booking.setStatus("WAITING");
        repository.save(booking);
        return booking;
    }
}