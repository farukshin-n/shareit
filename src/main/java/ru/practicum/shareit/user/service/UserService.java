package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.stream.Stream;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto getUser(long userId);

    Stream<UserDto> getAllUsers();

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);
}
