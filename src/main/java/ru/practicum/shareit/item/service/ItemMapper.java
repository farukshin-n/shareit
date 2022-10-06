package ru.practicum.shareit.item.service;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Data
@Component
public class ItemMapper {
    public static ItemDto itemToDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );
    }

    public static Item dtoToItem(ItemDto itemDto, long ownerId) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                ownerId);
    }
}
