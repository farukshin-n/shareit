package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemDto {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Name cannot be blank.")
    private String name;
    @NotBlank(groups = {Create.class})
    @Size(max = 200,
            message = "Description cannot be longer than 200 characters.",
            groups = {Create.class, Update.class})
    private String description;
    @NotNull(groups = {Create.class})
    private Boolean available;
    private UserDto owner;
    private Long requestId;
}
