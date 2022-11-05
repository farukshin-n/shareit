package ru.practicum.shareit.request.service;

import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toUserDto(itemRequest.getRequester()),
                itemRequest.getCreated()
        );
    }

    public static ItemRequest dtoToItemRequest(ItemRequestDto itemRequestDto, User owner) {
        return new ItemRequest(
                owner.getId(),
                itemRequestDto.getDescription(),
                owner,
                itemRequestDto.getCreated() == null ? LocalDateTime.now() : itemRequestDto.getCreated()
        );
    }

    public static ItemRequestDtoWithItems itemRequestToDtoWithItems(ItemRequest itemRequest,
                                                                    List<ItemDtoForRequests> items) {
        return new ItemRequestDtoWithItems(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items
        );
    }
}
