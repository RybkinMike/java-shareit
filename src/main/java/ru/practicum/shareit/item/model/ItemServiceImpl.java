package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.UserRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemRepository repository;
    UserRepository userRepository;
    ItemMapper itemMapper = new ItemMapper();

    @Override
    public Collection<Item> getItems(long userId) {
        userRepository.findById(userId);
        return repository.findByUserId(userId);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemMapper.toDto(repository.findById(itemId));
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        userRepository.findById(userId);
        itemDto.setId(itemId);
        Item item = itemMapper.toEntity(userId, itemDto);
        validate(userId, repository.findById(itemDto.getId()));
        return itemMapper.toDto(repository.update(item));
    }

    @Override
    public Item addNewItem(long userId, ItemDto itemDto) throws ValidationException {
        userRepository.findById(userId);
        Item item = itemMapper.toEntity(userId, itemDto);
        if (item.getAvailable() == null || !item.getAvailable()) {
            throw new ValidationException("Вещь должна быть доступна");
        }
        return repository.create(item);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        userRepository.findById(userId);
        validate(userId, repository.findById(itemId));
        repository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public List<ItemDto> getItemByQuery(String query) {
        if (query.isBlank()) {
            return new ArrayList<>();
        }
        return repository.getItemByQuery(query);
    }

    public void validate(long userId, Item item) {
        if (userId != item.getOwnerId()) {
            log.warn("Неправильный ID");
            throw new NotFoundException("Вы не являетесь владельцем.");
        }
    }
}
