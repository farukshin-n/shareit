package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping()
    public ResponseEntity<Object> postUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Creating user {}, userId={}", userDto, userDto.getId());
        return userClient.postUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> patchUser(@Validated({Update.class}) @PathVariable long userId,
                                            @RequestBody UserDto user) {
        log.info("Get request to update {} of user with id {}", user, userId);
        return userClient.patchUser(userId, user);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get request for getting all users list.");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Get request for getting user with id {}", userId);
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable long userId) {
        log.info("Get request for deleting user with id {}", userId);
        return userClient.deleteUserById(userId);
    }
}
