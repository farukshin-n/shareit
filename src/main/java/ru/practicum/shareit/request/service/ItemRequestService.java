package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(long id, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoWithItems> getRequests(long id);

    List<ItemRequestDtoWithItems> getAllRequests(long id, int from, int size);

    ItemRequestDtoWithItems getRequest(long id, long requestId);
}
