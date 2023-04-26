package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.*;

@Component
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRepositoryImpl implements ItemRepository {
    Map<Long, List<Item>> items = new HashMap<>();
    @NonFinal
    long counter = 0L;
    ItemMapper itemMapper = new ItemMapper();

    @Override
    public List<Item> findByUserId(long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Item findById(Long id) {
        for (List<Item> userItems: items.values()) {
            for (Item item: userItems) {
                if (item.getId().equals(id)) {
                    return item;
                }
            }
        }
       throw new NotFoundException(String.format("Вещь с ID =%d не найдена", id));
    }

    @Override
    public Item create(final Item item) {
        item.setId(getId());
        items.compute(item.getOwnerId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return item;
    }

    @Override
    public Item update(final Item item) {
        items.compute(item.getOwnerId(), (userId, userItems) -> {
            int index = -1;
            for (Item itemTemp : userItems) {
                if (itemTemp.getId().equals(item.getId())) {
                    index = userItems.indexOf(itemTemp);
                    if (item.getName() == null) {
                        item.setName(itemTemp.getName());
                    }
                    if (item.getDescription() == null) {
                        item.setDescription(itemTemp.getDescription());
                    }
                    if (item.getAvailable() == null) {
                        item.setAvailable(itemTemp.getAvailable());
                    }
                }
            }
            userItems.set(index, item);
            return userItems;
        });
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        if (items.containsKey(userId)) {
            List<Item> userItems = items.get(userId);
            userItems.removeIf(item -> item.getId().equals(itemId));
        }
    }

    private long getId() {
        long lastId = items.values()
                .stream()
                .flatMap(Collection::stream)
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }

    @Override
    public List<ItemDto> getItemByQuery(String query) {
        List<ItemDto> itemsByQuery = new ArrayList<>();
        for (List<Item> userItems: items.values()) {
            for (Item item: userItems) {
                if (item.getAvailable() && (item.getName().toUpperCase().contains(query.toUpperCase()) ||
                        item.getDescription().toUpperCase().contains(query.toUpperCase()))) {
                    itemsByQuery.add(itemMapper.toDto(item));
                }
            }
        }
        return itemsByQuery;
    }
}