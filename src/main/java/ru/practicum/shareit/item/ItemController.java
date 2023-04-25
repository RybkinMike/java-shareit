package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemService;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<Item> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getById(@RequestParam(name = "text") String query) {
        return itemService.getItemByQuery(query);
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") long userId,
                    @RequestBody @Valid ItemDto itemDto) throws ValidationException {
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}
