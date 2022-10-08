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

import java.util.List;
import java.util.stream.Collectors;

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
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            return ItemMapper.itemToDto(itemRepository.updateItem(item));
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
    public List<ItemDto> searchItem(String text) {
        return itemRepository.searchItems(text).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }
}
