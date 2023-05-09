package ru.practicum.shareit.item.model;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.Collection;
import java.util.List;

public interface ItemService {
    Item addNewItem(long userId, ItemDto itemDto) throws ValidationException;

    Collection<Item> getItems(long userId);

    ItemDto getItemById(long itemId);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    void deleteItem(long userId, long itemId);

    List<ItemDto> getItemByQuery(String query);
}