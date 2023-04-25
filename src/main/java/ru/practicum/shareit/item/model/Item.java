package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private Long count;
    private Long ownerId;
    private Boolean available;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) && Objects.equals(name, item.name) && Objects.equals(description, item.description) && Objects.equals(count, item.count) && Objects.equals(ownerId, item.ownerId) && Objects.equals(available, item.available);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, count, ownerId, available);
    }
}
