package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoWithItems> getRequests(long userId);

    List<ItemRequestDtoWithItems> getAllRequests(long userId, int from, int size);

    ItemRequestDtoWithItems getRequest(long userId, long requestId);
}
