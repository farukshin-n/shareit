package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    private final UserDto firstUser = new UserDto(1L, "Adam", "adam@paradise.com");
    private final UserDto secondUser = new UserDto(2L, "Eva", "eva@paradise.com");
    private final ItemRequestDto paradiseRequest = new ItemRequestDto(
            4L,
            "nice garden without people",
            firstUser,
            LocalDateTime.of(2022, 10, 20, 15, 55, 0)
    );
    private final ItemDto paradise = new ItemDto(
            3L,
            "Paradise",
            "great garden without people",
            true,
            secondUser,
            paradiseRequest.getId()
    );
    private final ItemDtoForRequests paradiseForRequests = new ItemDtoForRequests(
            paradise.getId(),
            paradise.getName(),
            paradise.getDescription(),
            paradise.getAvailable(),
            paradise.getRequestId()
    );
    private final ItemRequestDtoWithItems paradiseRequestWithItems = new ItemRequestDtoWithItems(
            paradiseRequest.getId(),
            paradiseRequest.getDescription(),
            paradiseRequest.getCreated(),
            List.of(paradiseForRequests)
    );
    private final String requestCreated = "2022-10-20T15:55:00";
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;

    @Test
    void handleAddRequest_byDefault() throws Exception {
        Mockito
                .when(itemRequestService.addRequest(anyLong(), any()))
                .thenReturn(paradiseRequest);

        mvc.perform(
                        post("/requests")
                                .header("X-Sharer-User-Id", firstUser.getId())
                                .content(mapper.writeValueAsString(paradiseRequest))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(paradiseRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(paradiseRequest.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(paradiseRequest.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requester.name", is(paradiseRequest.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(paradiseRequest.getRequester().getEmail())))
                .andExpect(jsonPath("$.created", is(requestCreated)));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .addRequest(1L, paradiseRequest);
    }

    @Test
    void handleGetRequests_byDefault() throws Exception {
        Mockito
                .when(itemRequestService.getRequests(anyLong()))
                .thenReturn(List.of(paradiseRequestWithItems));

        mvc.perform(
                        get("/requests")
                                .header("X-Sharer-User-Id", firstUser.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(paradiseRequestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(paradiseRequestWithItems.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(requestCreated)))
                .andExpect(jsonPath("$.[0].items[0].id",
                        is(paradiseRequestWithItems.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].items[0].name",
                        is(paradiseRequestWithItems.getItems().get(0).getName())))
                .andExpect(
                        jsonPath("$.[0].items[0].description",
                                is(paradiseRequestWithItems.getItems().get(0).getDescription()))
                )
                .andExpect(
                        jsonPath("$.[0].items[0].available",
                                is(paradiseRequestWithItems.getItems().get(0).isAvailable()))
                )
                .andExpect(
                        jsonPath("$.[0].items[0].requestId",
                                is(paradiseRequestWithItems.getItems().get(0).getRequestId()), Long.class
                        )
                );

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getRequests(1L);
    }

    @Test
    void handleGetAllRequests_byDefault() throws Exception {
        Mockito
                .when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(paradiseRequestWithItems));

        mvc.perform(
                        get("/requests/all")
                                .header("X-Sharer-User-Id", firstUser.getId())
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(paradiseRequestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(paradiseRequestWithItems.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(requestCreated)))
                .andExpect(jsonPath("$.[0].items[0].id",
                        is(paradiseRequestWithItems.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].items[0].name",
                        is(paradiseRequestWithItems.getItems().get(0).getName())))
                .andExpect(
                        jsonPath("$.[0].items[0].description",
                                is(paradiseRequestWithItems.getItems().get(0).getDescription()))
                )
                .andExpect(
                        jsonPath("$.[0].items[0].available",
                                is(paradiseRequestWithItems.getItems().get(0).isAvailable()))
                )
                .andExpect(
                        jsonPath(
                                "$.[0].items[0].requestId",
                                is(paradiseRequestWithItems.getItems().get(0).getRequestId()), Long.class
                        )
                );

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getAllRequests(1L, 0, 1);
    }

}
