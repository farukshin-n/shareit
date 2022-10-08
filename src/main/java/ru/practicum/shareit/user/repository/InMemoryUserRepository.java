package ru.practicum.shareit.user.repository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.SubstanceNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Getter
@Component
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long id = 1;

    @Override
    public User saveUser(User user) {
        final String email = user.getEmail();
        if (emails.contains(email)) {
            throw new DuplicateEmailException(email);
        }

        user.setId(generateId());
        emails.add(email);
        users.put(user.getId(), user);
        log.info("User with id {} is added to database.", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            final User oldUser = users.get(user.getId());
            final String email = user.getEmail();
            if (!email.equals(oldUser.getEmail()) && emails.contains(email)) {
                throw new DuplicateEmailException(email);
            }
            emails.remove(oldUser.getEmail());
            emails.add(email);
            users.replace(user.getId(), user);
            log.info("User with id {} is updated to database.", user.getId());
        } else {
            throw new SubstanceNotFoundException(String.format("User with id %d isn't exist", user.getId()));
        }
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        if (users.containsKey(userId)) {
            final User userToDelete = getUser(userId);
            final String email = userToDelete.getEmail();
            emails.remove(email);
            users.remove(userToDelete.getId());
            log.info("User with id {} is deleted from database.", userId);
        } else {
            throw new SubstanceNotFoundException(String.format("There isn't user with id %d", userId));
        }
    }

    @Override
    public User getUser(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new SubstanceNotFoundException(String.format("There isn't user with id %d.", userId));
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private long generateId() {
        return id++;
    }
}
