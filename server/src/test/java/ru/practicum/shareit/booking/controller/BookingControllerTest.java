package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.SubstanceNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private final User firstUser = new User(1L, "Adam", "adam@paradise.com");
    private final User secondUser = new User(2L, "Eva", "eva@paradise.com");
    private final Item paradise = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
    private final BookingDto bookingDto = new BookingDto(
            4L,
            LocalDateTime.of(2023, 10, 20, 12, 30),
            LocalDateTime.of(2023, 10, 21, 13, 35),
            paradise,
            firstUser,
            BookingStatus.WAITING
    );
    private final BookingDto approved = new BookingDto(
            bookingDto.getId(),
            bookingDto.getStart(),
            bookingDto.getEnd(),
            bookingDto.getItem(),
            bookingDto.getBooker(),
            BookingStatus.APPROVED
    );
    private final InputBookingDto inputBookingDto = new InputBookingDto(
            bookingDto.getStart(),
            bookingDto.getEnd(),
            bookingDto.getBooker().getId()
    );
    private final String startDate = "2023-10-20T12:30:00";
    private final String endDate = "2023-10-21T13:35:00";
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    @Test
    void handleAddBooking_byDefault() throws Exception {
        Mockito
                .when(bookingService.addBooking(anyLong(), any()))
                .thenReturn(bookingDto);

        mvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", firstUser.getId())
                                .content(mapper.writeValueAsString(inputBookingDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(startDate)))
                .andExpect(jsonPath("$.end", is(endDate)))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));

        Mockito
                .verify(bookingService, Mockito.times(1))
                .addBooking(1L, inputBookingDto);
    }

    @Test
    void handleAddBooking_withNotFound() throws Exception {
        Mockito
                .when(bookingService.addBooking(anyLong(), any()))
                .thenThrow(new SubstanceNotFoundException(
                        String.format("There isn't user with id %d in database.", 1L)));

        mvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", firstUser.getId())
                                .content(mapper.writeValueAsString(inputBookingDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void handleAddBooking_withItemUnavailable() throws Exception {
        Mockito
                .when(bookingService.addBooking(anyLong(), any()))
                .thenThrow(new NotAvailableException(
                        String.format("Item with id %d is not available for booking.", paradise.getId())
                ));

        mvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", firstUser.getId())
                                .content(mapper.writeValueAsString(inputBookingDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleAddBooking_withForbidden() throws Exception {
        Mockito
                .when(bookingService.addBooking(anyLong(), any()))
                .thenThrow(new SubstanceNotFoundException(
                        String.format("User with id %d cannot add item with %d", secondUser.getId(), paradise.getId()))
                );

        mvc.perform(
                        post("/bookings")
                                .header("X-Sharer-User-Id", secondUser.getId())
                                .content(mapper.writeValueAsString(inputBookingDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void handleChangeBookingStatus_byDefault() throws Exception {
        Mockito
                .when(bookingService.changeBookingStatus(anyLong(), anyLong(), eq(true)))
                .thenReturn(approved);

        mvc.perform(
                        patch("/bookings/{bookingId}", bookingDto.getId())
                                .header("X-Sharer-User-Id", secondUser.getId())
                                .param("approved", "true")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(approved.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(startDate)))
                .andExpect(jsonPath("$.end", is(endDate)))
                .andExpect(jsonPath("$.item.id", is(approved.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(approved.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(approved.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(approved.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(approved.getStatus().name())));

        Mockito.verify(bookingService, Mockito.times(1))
                .changeBookingStatus(2L, 4L, true);
    }

    @Test
    void handleGetBooking_byDefault() throws Exception {
        Mockito
                .when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(
                        get("/bookings/{bookingId}", bookingDto.getId())
                                .header("X-Sharer-User-Id", firstUser.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(startDate)))
                .andExpect(jsonPath("$.end", is(endDate)))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getBooking(1L, 4L);
    }

    @Test
    void handleGetUserBookings_byDefault() throws Exception {
        Mockito
                .when(bookingService.getUserBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", firstUser.getId())
                                .param("state", "ALL")
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(startDate)))
                .andExpect(jsonPath("$.[0].end", is(endDate)))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().name())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getUserBookings(1L, BookingState.ALL, 0, 1);
    }

    @Test
    void handleGetUserBookedItems_byDefault() throws Exception {
        Mockito
                .when(bookingService.getOwnerBookingList(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", firstUser.getId())
                                .param("state", "ALL")
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(startDate)))
                .andExpect(jsonPath("$.[0].end", is(endDate)))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().name())));
    }
}
