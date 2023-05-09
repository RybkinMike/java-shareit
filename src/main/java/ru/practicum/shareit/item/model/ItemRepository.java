package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository {
    List<Item> findByUserId(long userId);

    Item findById(Long id);

    Item create(Item item);

    Item update(Item item);

    void deleteByUserIdAndItemId(long userId, long itemId);

    List<ItemDto> getItemByQuery(String query);
}
