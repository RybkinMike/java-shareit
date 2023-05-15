package ru.practicum.shareit.item.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByUserId(long userId);

    Optional<Item> findById(long id);

    void deleteByUserIdAndId(long userId, long itemId);

    List<Item> getItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String query, String query1);
}
