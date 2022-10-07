package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.SubstanceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        if (userRepository.getUser(userId) != null) {
            final Item item = ItemMapper.dtoToItem(itemDto, userId);
            return ItemMapper.itemToDto(itemRepository.addItem(item));
        } else {
            throw new SubstanceNotFoundException(String.format("There isn't user with id %d in database.", userId));
        }
    }

    @Override
    public ItemDto getItem(Long id) {
        return ItemMapper.itemToDto(itemRepository.getItem(id));
    }

    @Override
    public List<ItemDto> getAllItemsByUser(Long userId) {
        final List<Item> resultListItems = itemRepository.getAllItemsOfUser(userId);

        return resultListItems.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.getItem(itemId);
        if (ownerId.equals(item.getOwnerId())) {
            Item itemToUpdate = item.update(itemDto);
            final Item updatedItem = itemRepository.updateItem(itemToUpdate);
            return ItemMapper.itemToDto(updatedItem);
        } else {
            throw new ForbiddenException(String.format(
                    "User with id %d cannot edit item with %d",
                    ownerId,
                    itemId)
            );
        }
    }

    @Override
    public void deleteItem(Long ownerId, Long itemId) {
        if (ownerId.equals(itemRepository.getItem(itemId).getOwnerId())) {
            itemRepository.deleteItem(itemId);
        } else {
            throw new ForbiddenException(String.format(
                    "User with id %d cannot delete item with %d",
                    ownerId,
                    itemId)
            );
        }
    }

    @Override
    public Stream<ItemDto> searchItem(String text) {
        return itemRepository.searchItems(text)
                .map(ItemMapper::itemToDto);
    }
}
