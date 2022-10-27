package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.SubstanceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private final User testUser = new User(1L, "Hannah", "hannah@gmail.com");
    private final UserDto testUserDto = new UserDto(testUser.getId(), testUser.getName(), testUser.getEmail());
    @Mock
    UserRepository mockUserRepository;
    @InjectMocks
    UserServiceImpl userService;

    @Test
    void handleAddUser_byDefault() {
        Mockito
                .when(mockUserRepository.save(any()))
                .thenReturn(testUser);
        UserDto user = userService.addUser(testUserDto);

        assertNotNull(user);
        assertEquals(testUserDto, user);
    }

    @Test
    void handleGetUser_byDefault() {
        Mockito
                .when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        UserDto user = userService.getUser(testUser.getId());

        assertEquals(testUserDto, user);
    }

    @Test
    void handleGetUser_withUserNotExist() {
        Mockito
                .when(mockUserRepository.findById(50L)).thenReturn(Optional.empty());

        assertThrows(SubstanceNotFoundException.class, () -> userService.getUser(50L));
    }

    @Test
    void handleGetAllUsers_byDefault() {
        Mockito
                .when(mockUserRepository.findAll()).thenReturn(List.of(testUser));
        List<UserDto> users = userService.getAllUsers();

        assertEquals(List.of(testUserDto), users);
    }

    @Test
    void handleUpdateUser_byDefault() {
        User sam = new User(2L, "Sam", "sam@gmail.com");
        UserDto samToUpdate = new UserDto(sam.getId(), "samuel", "secondUserson@gmail.com");
        User updatedSam = new User(sam.getId(), samToUpdate.getName(), samToUpdate.getEmail());
        UserDto samDto = new UserDto(sam.getId(), samToUpdate.getName(), samToUpdate.getEmail());
        Mockito
                .when(mockUserRepository.findById(sam.getId()))
                .thenReturn(Optional.of(sam));
        Mockito
                .when(mockUserRepository.save(sam))
                .thenReturn(updatedSam);
        UserDto actualSam = userService.updateUser(sam.getId(), samToUpdate);

        assertEquals(samDto, actualSam);
    }

    @Test
    void handleUpdateUser_withNullsInUpdate() {
        User sam = new User(2L, "Sam", "sam@gmail.com");
        UserDto samsecondUserson = new UserDto(sam.getId(), sam.getName(), sam.getEmail());
        UserDto updatedSam = new UserDto(sam.getId(), null, null);
        Mockito
                .when(mockUserRepository.findById(sam.getId()))
                .thenReturn(Optional.of(sam));
        Mockito
                .when(mockUserRepository.save(sam))
                .thenReturn(sam);
        UserDto user = userService.updateUser(sam.getId(), updatedSam);

        assertEquals(samsecondUserson, user);
    }

    @Test
    void handleUpdateUser_withUserNotExist() {
        UserDto samsecondUserson = new UserDto(null, "samuel", "secondUserson@gmail.com");
        Mockito
                .when(mockUserRepository.findById(59L)).thenReturn(Optional.empty());

        assertThrows(SubstanceNotFoundException.class, () -> userService.updateUser(59L, samsecondUserson));
    }

    @Test
    void handleDeleteUser_byDefault() {
        User sam = new User(2L, "Sam", "sam@gmail.com");
        userService.deleteUser(sam.getId());
        Mockito
                .verify(mockUserRepository, Mockito.times(1)).deleteById(sam.getId());
    }
}
