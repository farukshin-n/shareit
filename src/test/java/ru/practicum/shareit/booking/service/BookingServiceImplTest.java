package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.SubstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final BookingService bookService;

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleAddBooking_ByDefault() {
        InputBookingDto inputBookingDto = new InputBookingDto(
                LocalDateTime.now().plusMonths(1),
                LocalDateTime.now().plusMonths(2),
                5L
        );

        BookingDto newBookingDto = bookService.addBooking(1L, inputBookingDto);
        assertEquals(newBookingDto.getId(), 1L);
        assertEquals(newBookingDto.getStatus(), BookingStatus.WAITING);
        assertEquals(newBookingDto.getId(), 1L);
        assertEquals(newBookingDto.getItem().getId(), 5L);
        assertEquals(newBookingDto.getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-without-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleAddBooking_withItemIsNotAvailable() {
        InputBookingDto newBookingDto = new InputBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                3L
        );

        assertThrows(
                NotAvailableException.class,
                () -> bookService.addBooking(1L, newBookingDto),
                "Expected addBooking() to throw NotAvailableException because the item is not available."
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleAddBooking_withUserDoesNotExist() {
        Long userId = 53L;
        InputBookingDto newBookingDto = new InputBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                3L
        );

        assertThrows(
                SubstanceNotFoundException.class,
                () -> bookService.addBooking(userId, newBookingDto),
                "Expected addBooking() to throw SubstanceNotFoundException because the user does not exist.");
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleAddBooking_withItemDoesNotExist() {
        InputBookingDto newBookingDto = new InputBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                51L
        );

        assertThrows(
                SubstanceNotFoundException.class,
                () -> bookService.addBooking(1L, newBookingDto),
                "Expected addBooking() to throw SubstanceNotFoundException because the item does not exist."
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleAddBooking_BookerIsOwner() {
        InputBookingDto newBookingDto = new InputBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                3L
        );

        assertThrows(
                SubstanceNotFoundException.class,
                () -> bookService.addBooking(1L, newBookingDto),
                "Expected addBooking() to throw SubstanceNotFoundException because the owner cannot book their item."
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetBooking_withUserIsOwner() {
        BookingDto actual = bookService.getBooking(1L, 7L);

        assertThat(actual).isNotNull();
        assertEquals(7L, actual.getId());
        assertEquals(LocalDateTime.of(2022, 10, 25, 12, 30), actual.getStart());
        assertEquals(LocalDateTime.of(2022, 10, 30, 13, 35), actual.getEnd());
        assertEquals(3L, actual.getItem().getId());
        assertEquals(1L, actual.getBooker().getId());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetBooking_withUserIsNeitherBookerNorOwner() {
        Long randomId = 50L;

        assertThrows(SubstanceNotFoundException.class,
                () -> bookService.getBooking(randomId, 7L));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetBooking_withUserIsBooker() {
        BookingDto actual = bookService.getBooking(1L, 7L);

        assertThat(actual).isNotNull();
        assertEquals(7L, actual.getId());
        assertEquals(LocalDateTime.of(2022, 10, 25, 12, 30), actual.getStart());
        assertEquals(LocalDateTime.of(2022, 10, 30, 13, 35), actual.getEnd());
        assertEquals(3L, actual.getItem().getId());
        assertEquals(1L, actual.getBooker().getId());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-without-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetBooking_withBookingDoesNotExist() {
        Long bookingId = 51L;

        assertThrows(
                SubstanceNotFoundException.class,
                () -> bookService.getBooking(1L, bookingId),
                "Expected getBooking() to throw SubstanceNotFoundException because the booking does not exist."
        );
    }


    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetUserBookings_withUserDoesNotExist() {
        Long randomId = 53L;

        assertThrows(
                SubstanceNotFoundException.class,
                () -> bookService.getUserBookings(randomId, BookingState.ALL, 0, 10)
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetUserBookings_withStateIsWaiting() {
        List<BookingDto> actual = bookService.getUserBookings(1L, BookingState.WAITING, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-rejected-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetUserBookings_withStateIsRejected() {
        List<BookingDto> actual = bookService.getUserBookings(1L, BookingState.REJECTED, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), BookingStatus.REJECTED);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-current-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetUserBookings_withStateIsCurrent() {
        List<BookingDto> actual = bookService.getUserBookings(1L, BookingState.CURRENT, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
        assertTrue(actual.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(actual.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-past-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetUserBookings_withStateIsPast() {
        List<BookingDto> actual = bookService.getUserBookings(1L, BookingState.PAST, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
        assertTrue(actual.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(actual.get(0).getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-future-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetUserBookings_withStateIsFuture() {
        List<BookingDto> actual = bookService.getUserBookings(1L, BookingState.FUTURE, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
        assertTrue(actual.get(0).getStart().isAfter(LocalDateTime.now()));
        assertTrue(actual.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetUserBookings_withStateIsAll() {
        List<BookingDto> actual = bookService.getUserBookings(1L, BookingState.ALL, 0, 1);

        assertThat(actual).isNotNull();
        assertEquals(7L, actual.get(0).getId());
        assertEquals(LocalDateTime.of(2022, 10, 25, 12, 30), actual.get(0).getStart());
        assertEquals(LocalDateTime.of(2022, 10, 30, 13, 35), actual.get(0).getEnd());
        assertEquals(3L, actual.get(0).getItem().getId());
        assertEquals(1L, actual.get(0).getBooker().getId());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetOwnerBookingList_withStateIsWaiting() {
        List<BookingDto> actual = bookService.getOwnerBookingList(1L, BookingState.WAITING, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-rejected-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetOwnerBookingList_withStateIsRejected() {
        List<BookingDto> actual = bookService.getOwnerBookingList(1L, BookingState.REJECTED, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), BookingStatus.REJECTED);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-current-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetOwnerBookingList_withStateIsCurrent() {
        List<BookingDto> actual = bookService.getOwnerBookingList(1L, BookingState.CURRENT, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
        assertTrue(actual.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(actual.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-past-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetOwnerBookingList_withStateIsPast() {
        List<BookingDto> actual = bookService.getOwnerBookingList(1L, BookingState.PAST, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
        assertTrue(actual.get(0).getStart().isBefore(LocalDateTime.now()));
        assertTrue(actual.get(0).getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-future-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetOwnerBookingList_withStateIsFuture() {
        List<BookingDto> actual = bookService.getOwnerBookingList(1L, BookingState.FUTURE, 0, 1);

        assertEquals(actual.get(0).getId(), 7L);
        assertEquals(actual.get(0).getStatus(), BookingStatus.WAITING);
        assertEquals(actual.get(0).getItem().getId(), 3L);
        assertEquals(actual.get(0).getBooker().getId(), 1L);
        assertTrue(actual.get(0).getStart().isAfter(LocalDateTime.now()));
        assertTrue(actual.get(0).getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleGetOwnerBookingList_withStateIsAll() {
        List<BookingDto> actual = bookService.getOwnerBookingList(1L, BookingState.ALL, 0, 1);

        assertThat(actual).isNotNull();
        assertEquals(7L, actual.get(0).getId());
        assertEquals(LocalDateTime.of(2022, 10, 25, 12, 30), actual.get(0).getStart());
        assertEquals(LocalDateTime.of(2022, 10, 30, 13, 35), actual.get(0).getEnd());
        assertEquals(3L, actual.get(0).getItem().getId());
        assertEquals(1L, actual.get(0).getBooker().getId());
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleChangeBookingStatus_byDefault() {
        BookingDto actual = bookService.changeBookingStatus(1L, 7L, true);

        assertEquals(actual.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleChangeBookingStatus_withBookingDoesNotExist() {
        Long bookingId = 53L;

        assertThrows(
                SubstanceNotFoundException.class,
                () -> bookService.changeBookingStatus(1L, bookingId, true)
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-one-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleChangeBookingStatus_wihtUserIsNotOwner() {
        Long randomId = 53L;

        assertThrows(
                ForbiddenException.class,
                () -> bookService.changeBookingStatus(randomId, 7L, true)
        );
    }

    @Test
    @SqlGroup({
            @Sql(value = {"booking-service-test-before-with-approved-booking.sql"}, executionPhase = BEFORE_TEST_METHOD)
    })
    void handleChangeBookingStatus_StatusIsTheSame() {
        assertThrows(
                ForbiddenException.class,
                () -> bookService.changeBookingStatus(2L, 7L, true)
        );
    }


}
