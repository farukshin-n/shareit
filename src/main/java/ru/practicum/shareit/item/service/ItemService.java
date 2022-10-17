package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {
    ItemDto createItem(Long id, ItemDto itemDto);

    ItemDto getItem(Long id);

    List<ItemDto> getAllItemsByUser(Long userId);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    void deleteItem(Long ownerId, Long itemId);

    List<ItemDto> searchItem(String text);
}
