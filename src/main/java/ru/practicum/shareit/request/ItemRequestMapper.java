package ru.practicum.shareit.request;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
public class ItemRequestMapper {
    public ItemRequest toEntity(long userId, ItemRequestDto dto, List<Item> items) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(dto.getId());
        itemRequest.setUserId(userId);
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(items);
        return itemRequest;
    }
}
