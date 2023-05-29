package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
                                     @RequestParam(defaultValue = "ALL") String state,
                                     @RequestParam(value = "from", defaultValue = "0")@Min(0) int from,
                                     @RequestParam(value = "size", defaultValue = "10")@Min(1) @Max(100) int size) {
        return bookingService.getByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<Booking> getByOwnerId(@RequestHeader(USERID) long userId,
                                      @RequestParam(defaultValue = "ALL") String state,
                                      @RequestParam(value = "from", defaultValue = "0")@Min(0) int from,
                                      @RequestParam(value = "size", defaultValue = "10")@Min(1) @Max(100) int size) {
        return bookingService.getByOwnerId(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public Booking approveBooking(@RequestHeader(USERID) long userId,
                                  @PathVariable long bookingId,
                                  @RequestParam boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @PostMapping
    public Booking add(@RequestHeader(USERID) long userId,
                       @RequestBody @Valid BookingDto bookingDto) {
        return bookingService.addNewBooking(userId, bookingDto);
    }
}
