package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserMapper;

public class ItemRequestMapper {
    public static ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
            itemRequest.getId(),
            itemRequest.getDescription(),
            UserMapper.toUserDto(itemRequest.getRequester()),
            itemRequest.getCreated()
        );
    }

    public static ItemRequest dtoToItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
            itemRequestDto.getId(),
            itemRequestDto.getDescription(),
            UserMapper.toUser(itemRequestDto.getRequester()),
            itemRequestDto.getCreated()
        );
    }
}
