package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository repository;
    ItemRepository itemRepository;
    ItemRequestMapper requestMapper = new ItemRequestMapper();

    UserService userService;

    @Override
    public ItemRequest addNewItemRequest(long userId, ItemRequestDto requestDto) throws ValidationException {
        userService.getById(userId);
        ItemRequest request = requestMapper.toEntity(userId, requestDto, null);
        return repository.save(request);
    }

    @Override
    public Collection<ItemRequest> getRequests(long userId) {
        validateUserId(userId);
        List<ItemRequest> itemRequests = repository.findByUserId(userId);
        List<ItemRequest> itemRequestWithItems = mapToRequestWithItems(itemRequests);
        return itemRequestWithItems;
    }

    List<ItemRequest> mapToRequestWithItems(List<ItemRequest> itemRequests) {
        List<ItemRequest> itemRequestWithItems = new ArrayList<>();
        for (ItemRequest request: itemRequests) {
            List<Item> items = itemRepository.findByRequestId(request.getId()).orElse(null);
            request.setItems(items);
            itemRequestWithItems.add(request);
        }
        return itemRequestWithItems;
    }

    @Override
    public List<ItemRequest> getAllRequest(long userId, long from, long size) throws ValidationException {
        if (from < 0) {
            throw new ValidationException("from is negative");
        }
        Sort sortByDate = Sort.by(Sort.Direction.ASC, "created");
        long pageIndex = from / size;
        Pageable page = PageRequest.of((int)pageIndex, (int) size, sortByDate);
        Page<ItemRequest> itemRequestPage = repository.findAll(page);
        List<ItemRequest> itemRequestList = itemRequestPage.toList();
        for (ItemRequest itemRequest : itemRequestList) {
            if (itemRequest.getUserId() == userId) {
                 if (itemRequestList.size() == 1) {
                     return new ArrayList<>();
                 }
                 itemRequestList.remove(itemRequest);
            }
        }
        return mapToRequestWithItems(itemRequestPage.toList());
    }

    @Override
    public ItemRequest getRequestById(long userId, long requestId) {
        validateUserId(userId);
        ItemRequest itemRequest = repository.findById(requestId).orElseThrow(() -> new NotFoundException(String.format("Запрос с ID =%d не найден", requestId)));
        List<Item> items = itemRepository.findByRequestId(requestId).orElse(null);
        itemRequest.setItems(items);
        return itemRequest;
    }

    private void validateUserId(long userId) {
        userService.getById(userId);
    }
}
