package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.stream.Stream;

public interface UserRepository {
    User saveUser(User user);

    User getUser(Long id);

    Stream<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(long userId);
}
