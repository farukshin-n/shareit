package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

@Data
@AllArgsConstructor
public class Item {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long ownerId;

    public Item update(ItemDto itemDto) {
        if (itemDto.getName() != null) {
            this.name = itemDto.getName();
        }
        if (itemDto.getDescription() != null) {
            this.description = itemDto.getDescription();
        }
        if (itemDto.getAvailable() != null) {
            this.available = itemDto.getAvailable();
        }
        return this;
    }
}
