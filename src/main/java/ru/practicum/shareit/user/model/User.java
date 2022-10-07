package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
    private final String name;
    private final String email;

    public User update(UserDto userDto) {
        if (userDto.getName() == null) {
            userDto.setName(name);
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(email);
        }
        return new User(id, userDto.getName(), userDto.getEmail()) ;
    }
}
