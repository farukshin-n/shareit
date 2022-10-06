package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping()
    public UserDto addUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Request for adding user {}.", userDto);
        return userService.addUser(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@Valid @PathVariable Long id) {
        log.info("Request for getting user with id {}.", id);
        return userService.getUser(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Request for getting all registered users");
        return userService.getAllUsers();
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id,
                              @Validated(Update.class) @RequestBody UserDto userDto){
        log.info("Request for updating user {} with id {}.", userDto, id);
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        log.info("User with id {} successfully deleted.", id);
    }
}
