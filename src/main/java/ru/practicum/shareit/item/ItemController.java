package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemService;
import ru.practicum.shareit.item.model.ItemWithBooking;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemController {
    ItemService itemService;
    static final String USERID = "X-Sharer-User-Id";

    @GetMapping
    public Collection<ItemWithBooking> get(@RequestHeader(USERID) long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBooking getById(@RequestHeader(USERID) long userId,
                           @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getById(@RequestParam(name = "text") String query) {
        return itemService.getItemByQuery(query);
    }

    @PostMapping
    public Item add(@RequestHeader(USERID) long userId,
                    @RequestBody @Valid ItemDto itemDto) throws ValidationException {
        return itemService.addNewItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public Comment addComment(@RequestHeader(USERID) long userId,
                           @RequestBody @Valid Comment comment,
                           @PathVariable long itemId) throws ValidationException {
        return itemService.addNewComment(userId, comment, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USERID) long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USERID) long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}