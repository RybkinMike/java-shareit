package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingController {
    BookingService bookingService;

    static final String USERID = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    public Booking getById(@RequestHeader(USERID) long userId,
                              @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getByUserId(@RequestHeader(USERID) long userId,
                                     @RequestParam(defaultValue = "ALL") String state) throws ValidationException {
        return bookingService.getByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getByOwnerId(@RequestHeader(USERID) long userId,
                                      @RequestParam(defaultValue = "ALL") String state) throws ValidationException {
        return bookingService.getByOwnerId(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public Booking approveBooking(@RequestHeader(USERID) long userId,
                                  @PathVariable long bookingId,
                                  @RequestParam boolean approved) throws ValidationException {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @PostMapping
    public Booking add(@RequestHeader(USERID) long userId,
                    @RequestBody @Valid BookingDto bookingDto) throws ValidationException {
        return bookingService.addNewBooking(userId, bookingDto);
    }
}
