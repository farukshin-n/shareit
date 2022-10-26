package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    private final UserDto firstUser = new UserDto(1L, "Adam", "adam@paradise.com");
    private final UserDto secondUser = new UserDto(2L, "Eva", "eva@paradise.com");
    private final ItemDto paradiseDto = new ItemDto(
            3L,
            "Paradise",
            "great garden without people",
            true,
            secondUser,
            1L
    );
    private final CommentDto comment = new CommentDto(
            4L,
            "awesome",
            paradiseDto.getId(),
            firstUser.getId(),
            firstUser.getName(),
            LocalDateTime.of(2022, 10, 25, 18, 1)
    );
    private final BookingDtoWithBookerId lastBooking = new BookingDtoWithBookerId(
            5L,
            LocalDateTime.of(2022, 10, 20, 12, 30),
            LocalDateTime.of(2022, 10, 21, 13, 35),
            paradiseDto,
            firstUser.getId(),
            BookingStatus.APPROVED
    );
    private final BookingDtoWithBookerId nextBooking = new BookingDtoWithBookerId(
            6L,
            LocalDateTime.of(2022, 10, 23, 12, 35),
            LocalDateTime.of(2022, 10, 24, 13, 0),
            paradiseDto,
            firstUser.getId(),
            BookingStatus.APPROVED
    );
    private final ItemDtoWithBookingsAndComments paradiseWithCommentsAndBookings = new ItemDtoWithBookingsAndComments(
            paradiseDto.getId(),
            paradiseDto.getName(),
            paradiseDto.getDescription(),
            paradiseDto.getAvailable(),
            paradiseDto.getOwner(),
            1L,
            List.of(comment),
            lastBooking,
            nextBooking
    );
    private final String commentCreated = "2022-10-25T18:01:00";
    private final String lastBookingStart = "2022-10-20T12:30:00";
    private final String lastBookingEnd = "2022-10-21T13:35:00";
    private final String nextBookingStart = "2022-10-23T12:35:00";
    private final String nextBookingEnd = "2022-10-24T13:00:00";
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    @Test
    void handleCreateItem() throws Exception {
        Mockito
                .when(itemService.createItem(anyLong(), any()))
                .thenReturn(paradiseDto);

        mvc.perform(
                        post("/items")
                                .header("X-Sharer-User-Id", firstUser.getId())
                                .content(mapper.writeValueAsString(paradiseDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(paradiseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(paradiseDto.getName())))
                .andExpect(jsonPath("$.description", is(paradiseDto.getDescription())))
                .andExpect(jsonPath("$.available", is(paradiseDto.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(paradiseDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(paradiseDto.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(paradiseDto.getOwner().getEmail())))
                .andExpect(jsonPath("$.requestId", is(paradiseDto.getRequestId()), Long.class));
    }

    @Test
    void handleAddComment() throws Exception {
        Mockito.when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(comment);

        mvc.perform(
                        post("/items/{itemId}/comment", paradiseDto.getId())
                                .header("X-Sharer-User-Id", firstUser.getId())
                                .content(mapper.writeValueAsString(comment))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.itemId", is(comment.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorId", is(comment.getAuthorId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentCreated)));
    }

    @Test
    void handleGetItem() throws Exception {
        Mockito
                .when(itemService.getItemDtoWithBookingsAndComments(anyLong(), anyLong()))
                .thenReturn(paradiseWithCommentsAndBookings);

        mvc.perform(
                        get("/items/{itemId}", paradiseDto.getId())
                                .header("X-Sharer-User-Id", secondUser.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(paradiseWithCommentsAndBookings.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(paradiseWithCommentsAndBookings.getName())))
                .andExpect(jsonPath("$.description", is(paradiseWithCommentsAndBookings.getDescription())))
                .andExpect(jsonPath("$.available", is(paradiseWithCommentsAndBookings.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(paradiseWithCommentsAndBookings
                        .getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(paradiseWithCommentsAndBookings
                        .getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(paradiseWithCommentsAndBookings
                        .getOwner().getEmail())))
                .andExpect(jsonPath("$.requestId", is(paradiseWithCommentsAndBookings
                        .getRequestId()), Long.class))
                .andExpect(
                        jsonPath("$.comments[0].id", is(paradiseWithCommentsAndBookings
                                .getComments().get(0).getId()), Long.class)
                )
                .andExpect(
                        jsonPath("$.comments[0].text", is(paradiseWithCommentsAndBookings
                                .getComments().get(0).getText()))
                )
                .andExpect(
                        jsonPath(
                                "$.comments[0].itemId",
                                is(paradiseWithCommentsAndBookings.getComments().get(0).getItemId()), Long.class
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.comments[0].authorId",
                                is(paradiseWithCommentsAndBookings.getComments().get(0).getAuthorId()), Long.class
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.comments[0].authorName",
                                is(paradiseWithCommentsAndBookings.getComments().get(0).getAuthorName())
                        )
                )
                .andExpect(jsonPath("$.comments[0].created", is(commentCreated)))


                .andExpect(
                        jsonPath("$.lastBooking.id", is(paradiseWithCommentsAndBookings
                                .getLastBooking().getId()), Long.class)
                )
                .andExpect(jsonPath("$.lastBooking.start", is(lastBookingStart)))
                .andExpect(jsonPath("$.lastBooking.end", is(lastBookingEnd)))
                .andExpect(
                        jsonPath(
                                "$.lastBooking.item.id",
                                is(paradiseWithCommentsAndBookings.getLastBooking().getItem().getId()), Long.class
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.lastBooking.bookerId",
                                is(paradiseWithCommentsAndBookings.getLastBooking().getBookerId()), Long.class)
                )


                .andExpect(
                        jsonPath("$.nextBooking.id", is(paradiseWithCommentsAndBookings
                                .getNextBooking().getId()), Long.class)
                )
                .andExpect(jsonPath("$.nextBooking.start", is(nextBookingStart)))
                .andExpect(jsonPath("$.nextBooking.end", is(nextBookingEnd)))
                .andExpect(
                        jsonPath(
                                "$.nextBooking.item.id",
                                is(paradiseWithCommentsAndBookings.getNextBooking().getItem().getId()), Long.class)
                )
                .andExpect(
                        jsonPath(
                                "$.nextBooking.bookerId",
                                is(paradiseWithCommentsAndBookings.getNextBooking().getBookerId()), Long.class
                        )
                );
    }

    @Test
    void handleGetAllItemsByUser() throws Exception {
        Mockito
                .when(itemService.getAllItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(paradiseWithCommentsAndBookings));

        mvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", secondUser.getId())
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(paradiseWithCommentsAndBookings.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(paradiseWithCommentsAndBookings.getName())))
                .andExpect(jsonPath("$.[0].description", is(paradiseWithCommentsAndBookings
                        .getDescription())))
                .andExpect(jsonPath("$.[0].available", is(paradiseWithCommentsAndBookings.getAvailable())))
                .andExpect(
                        jsonPath("$.[0].comments[0].id", is(paradiseWithCommentsAndBookings
                                        .getComments().get(0).getId()),
                                Long.class)
                )
                .andExpect(
                        jsonPath("$.[0].comments[0].text", is(paradiseWithCommentsAndBookings
                                .getComments().get(0).getText()))
                )
                .andExpect(
                        jsonPath(
                                "$.[0].comments[0].itemId",
                                is(paradiseWithCommentsAndBookings.getComments().get(0).getItemId()), Long.class
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.[0].comments[0].authorId",
                                is(paradiseWithCommentsAndBookings.getComments().get(0).getAuthorId()), Long.class
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.[0].comments[0].authorName",
                                is(paradiseWithCommentsAndBookings.getComments().get(0).getAuthorName())
                        )
                )
                .andExpect(jsonPath("$.[0].comments[0].created", is(commentCreated)))


                .andExpect(
                        jsonPath("$.[0].lastBooking.id", is(paradiseWithCommentsAndBookings
                                .getLastBooking().getId()), Long.class)
                )
                .andExpect(jsonPath("$.[0].lastBooking.start", is(lastBookingStart)))
                .andExpect(jsonPath("$.[0].lastBooking.end", is(lastBookingEnd)))
                .andExpect(
                        jsonPath(
                                "$.[0].lastBooking.item.id",
                                is(paradiseWithCommentsAndBookings.getLastBooking().getItem().getId()), Long.class
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.[0].lastBooking.bookerId",
                                is(paradiseWithCommentsAndBookings.getLastBooking().getBookerId()), Long.class)
                )


                .andExpect(
                        jsonPath("$.[0].nextBooking.id", is(paradiseWithCommentsAndBookings.getNextBooking().getId()), Long.class)
                )
                .andExpect(jsonPath("$.[0].nextBooking.start", is(nextBookingStart)))
                .andExpect(jsonPath("$.[0].nextBooking.end", is(nextBookingEnd)))
                .andExpect(
                        jsonPath(
                                "$.[0].nextBooking.item.id",
                                is(paradiseWithCommentsAndBookings.getNextBooking().getItem().getId()), Long.class)
                )
                .andExpect(
                        jsonPath(
                                "$.[0].nextBooking.bookerId",
                                is(paradiseWithCommentsAndBookings.getNextBooking().getBookerId()), Long.class
                        )
                );
    }

    @Test
    void handleUpdateItem() throws Exception {
        Mockito.when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(paradiseDto);

        mvc.perform(
                        patch("/items/{itemId}", paradiseDto.getId())
                                .header("X-Sharer-User-Id", secondUser.getId())
                                .content(mapper.writeValueAsString(paradiseDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(paradiseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(paradiseDto.getName())))
                .andExpect(jsonPath("$.description", is(paradiseDto.getDescription())))
                .andExpect(jsonPath("$.available", is(paradiseDto.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(paradiseDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(paradiseDto.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(paradiseDto.getOwner().getEmail())))
                .andExpect(jsonPath("$.requestId", is(paradiseDto.getRequestId()), Long.class));
    }

    @Test
    void handleDeleteItem() throws Exception {
        itemService.deleteItem(anyLong(), anyLong());

        Mockito
                .verify(itemService, Mockito.times(1))
                .deleteItem(anyLong(), anyLong());

        mvc.perform(
                        delete("/items/{itemId}", paradiseDto.getId())
                                .header("X-Sharer-User-Id", secondUser.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void handleSearchItem() throws Exception {
        Mockito
                .when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(paradiseDto));

        mvc.perform(
                        get("/items/search")
                                .param("text", "table")
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(paradiseDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(paradiseDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(paradiseDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(paradiseDto.getAvailable())))
                .andExpect(jsonPath("$.[0].owner.id", is(paradiseDto.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.[0].owner.name", is(paradiseDto.getOwner().getName())))
                .andExpect(jsonPath("$.[0].owner.email", is(paradiseDto.getOwner().getEmail())))
                .andExpect(jsonPath("$.[0].requestId", is(paradiseDto.getRequestId()), Long.class));
    }
}
