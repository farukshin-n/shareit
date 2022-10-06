package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.user.model.User;

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

    public User update(User user) {
        if (name == null) {
            this.name = user.getName();
        }
        if (email == null) {
            this.email = user.getEmail();
        }

        User resultUser = new User(this.name, this.email);
        resultUser.setId(user.getId());
        return resultUser;
    }
}
