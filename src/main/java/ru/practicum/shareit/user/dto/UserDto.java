package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {
    private Long id;
    @NotBlank(message = "Name cannot be blank", groups = {Create.class})
    private String name;
    @NotEmpty(groups = {Create.class})
    @Email(message = "Email should be correct", groups = {Create.class})
    private String email;
}
