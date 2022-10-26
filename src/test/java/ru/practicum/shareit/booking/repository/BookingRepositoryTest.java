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
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(value = {"booking-repository-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"booking-repository-test-after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final User firstUser = new User(1L, "Adam", "adam@paradise.com");
    private final User secondUser = new User(2L, "Eva", "eva@paradise.com");
    private final Item paradise = new Item(3L,
            "Paradise",
            "great garden without people",
            true,
            firstUser,
            null);
    private final Booking booking = new Booking(
            4L,
            LocalDateTime.of(2021, 10, 20, 12, 30),
            LocalDateTime.of(2021, 10, 21, 13, 35),
            paradise,
            firstUser,
            BookingStatus.WAITING);
    private final Item apple = new Item(5L, "Apple", "very tasty fruit", true, secondUser, null);
    private final Booking bookingCurrent = new Booking(
            7L,
            LocalDateTime.of(2022, 10, 20, 12, 30),
            LocalDateTime.of(2022, 10, 21, 13, 35),
            apple,
            secondUser,
            BookingStatus.WAITING);
    private final Booking bookingPast = new Booking(
            6L,
            LocalDateTime.of(2021, 10, 23, 12, 35),
            LocalDateTime.of(2021, 10, 24, 13, 0),
            apple,
            secondUser,
            BookingStatus.WAITING);
    private final Set<Long> setIds = Set.of(4L, 6L, 7L);

    @Test
    void handleGetBookingsByOwnerId() {
        List<Booking> result = bookingRepository.getBookingsByOwnerId(firstUser.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void handleGetBookingsByUserItemsWithState() {
        List<Booking> result = bookingRepository.getBookingsByUserItemsWithState(
                firstUser.getId(),
                BookingStatus.WAITING,
                Pageable.unpaged()
        );

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void handleGetBookingsByOwnerIdCurrent() {
        List<Booking> result = bookingRepository.getBookingsByOwnerIdCurrent(secondUser.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(bookingCurrent));
    }

    @Test
    void handleGetBookingsByOwnerIdPast() {
        List<Booking> result = bookingRepository.getBookingsByOwnerIdPast(secondUser.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(bookingPast));
    }

    @Test
    void handleGetBookingsByOwnerIdFuture() {
        List<Booking> result = bookingRepository.getBookingsByOwnerIdFuture(firstUser.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void handleGetPastOrCurrentBookingByItemId() {
        Optional<Booking> result = bookingRepository.getPastOrCurrentBookingByItemId(apple.getId());

        assertThat(result).isNotEmpty().isEqualTo(Optional.of(bookingCurrent));
    }

    @Test
    void handleGetBookingsByItem_IdInOrderByEndAsc() {
        List<Booking> result = bookingRepository.getBookingsByItem_IdInOrderByEndAsc(setIds);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0)).isEqualTo(booking);
        assertThat(result.get(1)).isEqualTo(bookingPast);
        assertThat(result.get(2)).isEqualTo(bookingCurrent);
    }

    @Test
    void handleGetFutureBookingByItemId() {
        Optional<Booking> result = bookingRepository.getFutureBookingByItemId(paradise.getId());

        assertThat(result).isPresent().isEqualTo(Optional.of(booking));
    }

    @Test
    void handleFindBookingsByBooker_Id() {
        List<Booking> result = bookingRepository.findBookingsByBooker_Id(secondUser.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void handleFindBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter() {
        List<Booking> result = bookingRepository.findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(
                secondUser.getId(),
                LocalDateTime.of(2021, 10, 20, 18, 30, 0),
                LocalDateTime.of(2021, 10, 20, 19, 30, 0),
                Pageable.unpaged()
        );

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void handleFindBookingsByBooker_IdAndEndIsBefore() {
        List<Booking> result = bookingRepository.findBookingsByBooker_IdAndEndIsBefore(
                secondUser.getId(),
                LocalDateTime.of(2021, 10, 22, 12, 30, 0),
                Pageable.unpaged()
        );

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void findBookingsByBooker_IdAndStartIsAfter() {
        List<Booking> result = bookingRepository.findBookingsByBooker_IdAndStartIsAfter(
                secondUser.getId(),
                LocalDateTime.of(2021, 10, 19, 12, 30, 0),
                Pageable.unpaged()
        );

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void handleFindBookingsByBooker_IdAndStatus() {
        List<Booking> result = bookingRepository.findBookingsByBooker_IdAndStatus(
                secondUser.getId(),
                BookingStatus.WAITING,
                Pageable.unpaged()
        );

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void handleFindBookingsByItem_IdAndBooker_IdAndEndIsBefore() {
        List<Booking> result = bookingRepository.findBookingsByItem_IdAndBooker_IdAndEndIsBefore(
                paradise.getId(),
                secondUser.getId(),
                LocalDateTime.of(2021, 10, 22, 13, 35)
        );

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }
}
