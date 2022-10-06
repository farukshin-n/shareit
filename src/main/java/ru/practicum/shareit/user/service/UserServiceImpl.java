package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
    public List<UserDto> getAllUsers() {
        final List<User> userList = userRepository.getAllUsers();
        List<UserDto> resultList = new ArrayList<>();
        for (User user : userList) {
            resultList.add(UserMapper.toUserDto(user));
        }
        return resultList;
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.getUser(userId);
        User userToUpdate = userDto.update(user);
        return UserMapper.toUserDto(userRepository.updateUser(userToUpdate));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
        log.info(String.format("User with id %d deleted.", userId));
    }
}
