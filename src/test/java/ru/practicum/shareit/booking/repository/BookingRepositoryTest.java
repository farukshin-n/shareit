package ru.practicum.shareit.booking.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@SqlGroup({
        @Sql(value = {"booking-repository-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"booking-repository-test-after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final User firstUser = new User(1L, "Adam", "adam@paradise.com");
    private final User secondUser = new User(2L, "Eva", "eva@paradise.com");
    private final ItemRequest request = new ItemRequest(
            4L,
            "great garden without people",
            secondUser,
            LocalDateTime.of(2022, 10, 10, 12, 0));
    private final Item paradise = new Item(3L,
            "Paradise",
            "great garden without people",
            true,
            firstUser,
            request);
    private final Booking booking = new Booking(
            4L,
            LocalDateTime.of(2023, 10, 20, 12, 30),
            LocalDateTime.of(2023, 10, 21, 13, 35),
            paradise,
            secondUser,
            BookingStatus.WAITING);
    private final Item apple = new Item(5L, "Apple", "very tasty fruit", true, secondUser, null);
    private final Booking bookingCurrent = new Booking(
            7L,
            LocalDateTime.of(2022, 10, 25, 12, 30),
            LocalDateTime.of(2022, 10, 30, 13, 35),
            apple,
            firstUser,
            BookingStatus.WAITING);
    private final Booking bookingPast = new Booking(
            6L,
            LocalDateTime.of(2021, 10, 27, 12, 35),
            LocalDateTime.of(2021, 10, 28, 13, 0),
            apple,
            firstUser,
            BookingStatus.WAITING);
    private final Set<Long> setIds = Set.of(3L, 5L);

    @Test
    void handleGetBookingsByOwnerId() {
        List<Booking> result = bookingRepository.getBookingsByOwnerId(firstUser.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getBooker()).isEqualTo(booking.getBooker());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void handleGetBookingsByUserItemsWithState() {
        List<Booking> result = bookingRepository.getBookingsByUserItemsWithState(
                firstUser.getId(),
                BookingStatus.WAITING,
                Pageable.unpaged()
        );

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getBooker()).isEqualTo(booking.getBooker());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void handleGetBookingsByOwnerIdCurrent() {
        List<Booking> result = bookingRepository.getBookingsByOwnerIdCurrent(secondUser.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(bookingCurrent.getId());
        assertThat(result.get(0).getStart()).isEqualTo(bookingCurrent.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(bookingCurrent.getEnd());
        assertThat(result.get(0).getStatus()).isEqualTo(bookingCurrent.getStatus());
    }

    @Test
    void handleGetBookingsByOwnerIdPast() {
        List<Booking> result = bookingRepository.getBookingsByOwnerIdPast(secondUser.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(bookingPast.getId());
        assertThat(result.get(0).getStart()).isEqualTo(bookingPast.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(bookingPast.getEnd());
        assertThat(result.get(0).getStatus()).isEqualTo(bookingPast.getStatus());
    }

    @Test
    void handleGetBookingsByOwnerIdFuture() {
        List<Booking> result = bookingRepository.getBookingsByOwnerIdFuture(firstUser.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getBooker()).isEqualTo(booking.getBooker());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void handleGetPastOrCurrentBookingByItemId() {
        Optional<Booking> result = bookingRepository.getPastOrCurrentBookingByItemId(apple.getId());

        assertThat(result).isNotEmpty();
        assertThat(result.get().getId()).isEqualTo(bookingCurrent.getId());
        assertThat(result.get().getStart()).isEqualTo(bookingCurrent.getStart());
        assertThat(result.get().getEnd()).isEqualTo(bookingCurrent.getEnd());
        assertThat(result.get().getStatus()).isEqualTo(bookingCurrent.getStatus());
    }

    @Test
    void handleGetBookingsByItem_IdInOrderByEndAsc() {
        List<Booking> result = bookingRepository.getBookingsByItem_IdInOrderByEndAsc(setIds);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(bookingPast.getId());
        assertThat(result.get(1).getId()).isEqualTo(bookingCurrent.getId());
        assertThat(result.get(2).getId()).isEqualTo(booking.getId());
    }

    @Test
    void handleGetFutureBookingByItemId() {
        Optional<Booking> result = bookingRepository.getFutureBookingByItemId(paradise.getId());

        assertThat(result).isNotEmpty();
        assertThat(result.get().getId()).isEqualTo(booking.getId());
        assertThat(result.get().getStart()).isEqualTo(booking.getStart());
        assertThat(result.get().getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get().getBooker()).isEqualTo(booking.getBooker());
        assertThat(result.get().getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void handleFindBookingsByBooker_Id() {
        List<Booking> result = bookingRepository.findBookingsByBooker_Id(secondUser.getId(), Pageable.unpaged());
        // or first?
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getBooker()).isEqualTo(booking.getBooker());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void handleFindBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter() {
        List<Booking> result = bookingRepository.findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(
                secondUser.getId(),
                LocalDateTime.of(2023, 10, 25, 18, 30, 0),
                LocalDateTime.of(2023, 10, 20, 19, 30, 0),
                Pageable.unpaged()
        );

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getBooker()).isEqualTo(booking.getBooker());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void handleFindBookingsByBooker_IdAndEndIsBefore() {
        List<Booking> result = bookingRepository.findBookingsByBooker_IdAndEndIsBefore(
                secondUser.getId(),
                LocalDateTime.of(2024, 10, 22, 12, 30, 0),
                Pageable.unpaged()
        );

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getBooker()).isEqualTo(booking.getBooker());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void findBookingsByBooker_IdAndStartIsAfter() {
        List<Booking> result = bookingRepository.findBookingsByBooker_IdAndStartIsAfter(
                secondUser.getId(),
                LocalDateTime.of(2021, 10, 19, 12, 30, 0),
                Pageable.unpaged()
        );

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getBooker()).isEqualTo(booking.getBooker());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void handleFindBookingsByBooker_IdAndStatus() {
        List<Booking> result = bookingRepository.findBookingsByBooker_IdAndStatus(
                secondUser.getId(),
                BookingStatus.WAITING,
                Pageable.unpaged()
        );

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getBooker()).isEqualTo(booking.getBooker());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void handleFindBookingsByItem_IdAndBooker_IdAndEndIsBefore() {
        List<Booking> result = bookingRepository.findBookingsByItem_IdAndBooker_IdAndEndIsBefore(
                paradise.getId(),
                secondUser.getId(),
                LocalDateTime.of(2024, 10, 22, 13, 35)
        );

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo(booking.getId());
        assertThat(result.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(result.get(0).getEnd()).isEqualTo(booking.getEnd());
        assertThat(result.get(0).getBooker()).isEqualTo(booking.getBooker());
        assertThat(result.get(0).getStatus()).isEqualTo(booking.getStatus());
    }
}
