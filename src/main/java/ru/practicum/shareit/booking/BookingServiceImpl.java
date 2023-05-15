package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {

    ItemRepository itemRepository;

    UserRepository userRepository;

    BookingRepository repository;

    BookingMapper bookingMapper = new BookingMapper();

    @Override
    public Booking getBookingById(long userId, long bookingId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Booking  booking = repository.findById(bookingId).orElseThrow(() -> new NotFoundException(String.format("Бронь с ID =%d не найден", bookingId)));
        User user = booking.getBooker();
        User owner = booking.getItem().getUser();
        if (userId == user.getId() || userId == owner.getId()) {
            return booking;
        }
        throw new NotFoundException(String.format("Пользователь с ID =%d не является ни создателем брони, ни владельцем вещи", userId));
    }

    @Override
    public List<Booking> getByUserId(long userId, String state) throws ValidationException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        switch (state) {
            case "ALL":
                return repository.findByUserId(userId);
            case "CURRENT":
                return repository.getCurrentByUserId(userId);
            case "PAST":
                return repository.getBookingByUserIdAndFinishAfterNow(userId);
            case "FUTURE":
                return repository.getBookingByUserIdAndStarBeforeNow(userId);
            case "WAITING":
            case "REJECTED":
                return repository.getBookingByUserIdAndByStatusContainingIgnoreCase(userId, state);
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<Booking> getByOwnerId(long userId, String state) throws ValidationException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        switch (state) {
            case "ALL":
                return repository.findByOwnerId(userId);
            case "CURRENT":
                return repository.getCurrentByOwnerId(userId);
            case "PAST":
                return repository.getPastByOwnerId(userId);
            case "FUTURE":
                return repository.getBookingByOwnerIdAndStarBeforeNow(userId);
            case "WAITING":
            case "REJECTED":
                return repository.getBookingByOwnerIdAndByStatusContainingIgnoreCase(userId, state);
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Transactional
    @Override
    public Booking approveBooking(long userId, long bookingId, boolean approve) throws ValidationException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Booking  booking = repository.findById(bookingId).orElseThrow(() -> new NotFoundException(String.format("Бронь с ID =%d не найден", bookingId)));
        if (booking.getStatus().equals("APPROVED")) {
            throw new ValidationException(String.format("Бронь с ID =%d уже подтверждена", bookingId));
        }
        User owner = booking.getItem().getUser();
        if (userId != (owner.getId())) {
            throw new NotFoundException(String.format("Пользователь с ID =%d не является ни создателем брони, ни владельцем вещи", userId));
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
    public Booking addNewBooking(long userId, BookingDto bookingDto) throws ValidationException {
        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new ValidationException("Окончание бронирования должно быть после его начала");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));
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