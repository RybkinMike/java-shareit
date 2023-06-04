package ru.practicum.shareit.item.model;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ItemMapper {

    public Item toEntity(User user, ItemDto dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setCount(dto.getCount());
        item.setUser(user);
        item.setAvailable(dto.getAvailable());
        item.setRequestId(dto.getRequestId());
        return item;
    }

    public ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getCount(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

    public ItemWithBooking toEntityWithBooking(Item item, BookingDto lastBooking, BookingDto nextBooking, List<Comment> comments) {
        ItemWithBooking itemWithBooking = new ItemWithBooking();
        itemWithBooking.setId(item.getId());
        itemWithBooking.setName(item.getName());
        itemWithBooking.setDescription(item.getDescription());
        itemWithBooking.setCount(item.getCount());
        itemWithBooking.setUser(item.getUser());
        itemWithBooking.setAvailable(item.getAvailable());
        itemWithBooking.setLastBooking(lastBooking);
        itemWithBooking.setNextBooking(nextBooking);
        itemWithBooking.setComments(comments);
        itemWithBooking.setRequestId(item.getRequestId());

        return itemWithBooking;
    }

    public List<ItemDto> mapToItemDto(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toDto(item));
        }
        return dtos;
    }
}
