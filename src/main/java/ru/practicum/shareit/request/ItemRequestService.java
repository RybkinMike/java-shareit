package ru.practicum.shareit.request;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;
import java.util.List;

@Transactional(readOnly = true)
public interface ItemRequestService {
    @Transactional
    ItemRequest addNewItemRequest(long userId, ItemRequestDto requestDto) throws ValidationException;

    Collection<ItemRequest> getRequests(long userId);

    List<ItemRequest> getAllRequest(long userId, long from, long size) throws ValidationException;

    ItemRequest getRequestById(long userId, long requestId);

//    void updateRequest(Item item, long requestId);
}
