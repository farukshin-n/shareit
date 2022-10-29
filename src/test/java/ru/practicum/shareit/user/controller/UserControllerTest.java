package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    private final UserDto samsecondUserson = new UserDto(1L, "samuel", "secondUserson@gmail.com");
    private final UserDto updatesecondUserson = new UserDto(null, "samuel", "secondUserson@gmail.com");
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    void handleAddUser_byDefault() throws Exception {
        Mockito
                .when(userService.addUser(any()))
                .thenReturn(samsecondUserson);

        mvc.perform(
                post("/users")
                        .content(mapper.writeValueAsString(samsecondUserson))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(samsecondUserson.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(samsecondUserson.getName())))
                .andExpect(jsonPath("$.email", is(samsecondUserson.getEmail())));


        Mockito.verify(userService, Mockito.times(1))
                .addUser(samsecondUserson);
    }

    @Test
    void handleGetUser_byDefault() throws Exception {
        Mockito
                .when(userService.getUser(anyLong()))
                .thenReturn(samsecondUserson);

        mvc.perform(
                        get("/users/{id}", samsecondUserson.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(samsecondUserson.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(samsecondUserson.getName())))
                .andExpect(jsonPath("$.email", is(samsecondUserson.getEmail())));

        Mockito.verify(userService, Mockito.times(1))
                .getUser(1L);
    }

    @Test
    void handleGetAllUsers_byDefault() throws Exception {
        Mockito
                .when(userService.getAllUsers())
                .thenReturn(List.of(samsecondUserson));

        mvc.perform(
                        get("/users")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(samsecondUserson.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(samsecondUserson.getName())))
                .andExpect(jsonPath("$.[0].email", is(samsecondUserson.getEmail())));

        Mockito.verify(userService, Mockito.times(1))
                .getAllUsers();
    }

    @Test
    void handleUpdateUser_byDefault() throws Exception {
        Mockito
                .when(userService.updateUser(anyLong(), any()))
                .thenReturn(samsecondUserson);

        mvc.perform(
                        patch("/users/{id}", samsecondUserson.getId())
                                .content(mapper.writeValueAsString(updatesecondUserson))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(samsecondUserson.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(samsecondUserson.getName())))
                .andExpect(jsonPath("$.email", is(samsecondUserson.getEmail())));

        Mockito.verify(userService, Mockito.times(1))
                .updateUser(1L, updatesecondUserson);
    }

    @Test
    void handleDeleteUser_byDefault() throws Exception {
        userService.deleteUser(anyLong());
        Mockito
                .verify(userService, times(1))
                .deleteUser(anyLong());

        mvc.perform(
                        delete("/users/{id}", samsecondUserson.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }
}
