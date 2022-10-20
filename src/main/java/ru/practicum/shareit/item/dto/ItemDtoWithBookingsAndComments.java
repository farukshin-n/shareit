package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDtoWithBookingsAndComments {
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
    private List<CommentDto> comments;
    private BookingDtoWithBookerId lastBooking;
    private BookingDtoWithBookerId nextBooking;
}
