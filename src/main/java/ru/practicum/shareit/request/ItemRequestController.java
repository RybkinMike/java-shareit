package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestController {
    ItemRequestService itemRequestService;
    static final String USERID = "X-Sharer-User-Id";

    @GetMapping
    public Collection<ItemRequest> getItemRequest(@RequestHeader(USERID) long userId) {
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequest getItemRequestById(@RequestHeader(USERID) long userId,
                                          @PathVariable long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequest> getAllItemRequest(@RequestHeader(USERID) long userId,
                                               @RequestParam(value = "from", defaultValue = "0")@Min(0) int from,
                                               @RequestParam(value = "size", defaultValue = "10")@Min(1) @Max(100) int size) {
        return itemRequestService.getAllRequest(userId, from, size);
    }

    @PostMapping
    public ItemRequest add(@RequestHeader(USERID) long userId,
                    @RequestBody @Valid ItemRequestDto requestDto) {
        return itemRequestService.addNewItemRequest(userId, requestDto);
    }
}