package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class Item {
    private long id;
    @NotBlank(message = "Item name cannot be blank")
    private final String name;
    @Size(max = 200, message = "Item description cannot be longer than 200 characters")
    private final String description;
    @NotNull(message = "Item status cannot be null")
    private final boolean available;
    @NotNull(message = "Item owner cannot be null")
    private final long ownerId;
}
