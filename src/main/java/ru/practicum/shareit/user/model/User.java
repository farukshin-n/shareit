package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@RequiredArgsConstructor
public class User {
    private long id;
    @NotBlank(message = "Name cannot be blank", groups = {Create.class})
    private final String name;
    @NotEmpty(groups = {Create.class})
    @Email(message = "Email should be correct", groups = {Create.class})
    private final String email;
}
