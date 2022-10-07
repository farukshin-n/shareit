package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        final User user = UserMapper.toUser(userDto);
        final User newUser = userRepository.saveUser(user);
        final UserDto newUserDto = UserMapper.toUserDto(newUser);
        log.info("New user created.");

        return newUserDto;
    }

    @Override
    public UserDto getUser(long userId) {
        final User user = userRepository.getUser(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public Stream<UserDto> getAllUsers() {
        return userRepository.getAllUsers().map(UserMapper::toUserDto);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.getUser(userId);
        User userToUpdate = user.update(userDto);
        return UserMapper.toUserDto(userRepository.updateUser(userToUpdate));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
        log.info(String.format("User with id %d deleted.", userId));
    }
}
