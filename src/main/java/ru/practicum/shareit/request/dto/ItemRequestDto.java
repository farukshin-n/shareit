package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ItemRequestDto {
    private Long id;
    @Size(max = 200,
            message = "Description cannot be longer than 200 characters.",
            groups = {Create.class})
    @NotBlank
    private String description;
    private UserDto requester;
    private LocalDateTime created;
}
