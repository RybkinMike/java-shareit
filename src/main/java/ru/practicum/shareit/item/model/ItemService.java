package ru.practicum.shareit.item.model;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.Collection;
import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    @Transactional
    Item addNewItem(long userId, ItemDto itemDto) throws ValidationException;

    Collection<ItemWithBooking> getItems(long userId);

    ItemWithBooking getItemById(long userId, long itemId);

    Item getItemById(long itemId);

    @Transactional
    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    @Transactional
    void deleteItem(long userId, long itemId);

    @Transactional
    Comment addNewComment(long userId, Comment comment, long itemId) throws ValidationException;

    List<ItemDto> getItemByQuery(String query);
}