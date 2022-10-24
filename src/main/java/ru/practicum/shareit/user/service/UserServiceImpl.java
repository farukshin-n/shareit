package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.SubstanceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        final User user = UserMapper.toUser(userDto);
        final User newUser = userRepository.save(user);
        final UserDto newUserDto = UserMapper.toUserDto(newUser);
        log.info("New user created.");

        return newUserDto;
    }

    @Override
    public UserDto getUser(long userId) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't user with id %d in database.", userId)));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't user with id %d in database.", userId)));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        User newUser = userRepository.save(user);
        log.info("User with id {}, name {}, email {} is updated.", userId, newUser.getName(), newUser.getEmail());
        return UserMapper.toUserDto(newUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info(String.format("User with id %d deleted.", userId));
    }
}
