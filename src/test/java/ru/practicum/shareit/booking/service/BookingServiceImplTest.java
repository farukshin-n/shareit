package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.SubstanceNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    BookingRepository mockBookingRepository;
    @Mock
    UserRepository mockUserRepository;
    @Mock
    ItemRepository mockItemRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void handleAddBooking_byDefault() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", true, secondUser, null);
        InputBookingDto newBookingDto = new InputBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                item.getId()
        );
        Booking booking = new Booking(
                4L,
                newBookingDto.getStart(),
                newBookingDto.getEnd(),
                item,
                firstUser,
                BookingStatus.WAITING
        );
        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                item.getOwner(),
                BookingStatus.WAITING
        );
        Mockito.when(mockUserRepository.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));
        Mockito.when(mockItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(mockBookingRepository.save(any())).thenReturn(booking);
        BookingDto actual = bookingService.addBooking(firstUser.getId(), newBookingDto);

        assertEquals(expected, actual);
    }

    @Test
    void handleAddBooking_withItemIsNotAvailable() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        InputBookingDto newBookingDto = new InputBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                item.getId()
        );
        Mockito.when(mockUserRepository.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));
        Mockito.when(mockItemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(
                NotAvailableException.class,
                () -> bookingService.addBooking(firstUser.getId(), newBookingDto),
                "Expected addBooking() to throw NotAvailableException because the item is not available."
        );
    }

    @Test
    void handleAddBooking_withUserDoesNotExist() {
        Long userId = 53L;
        InputBookingDto newBookingDto = new InputBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                2L
        );
        Mockito
                .when(mockUserRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                SubstanceNotFoundException.class,
                () -> bookingService.addBooking(userId, newBookingDto),
                "Expected addBooking() to throw SubstanceNotFoundException because the user does not exist.");
    }

    @Test
    void handleAddBooking_withItemDoesNotExist() {
        Long itemId = 51L;
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        InputBookingDto newBookingDto = new InputBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                itemId
        );
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(mockItemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(
                SubstanceNotFoundException.class,
                () -> bookingService.addBooking(firstUser.getId(), newBookingDto),
                "Expected addBooking() to throw SubstanceNotFoundException because the item does not exist."
        );
    }

    @Test
    void handleAddBooking_BookerIsOwner() {
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(2L, "Paradise", "nice garden without people", true, secondUser, null);
        InputBookingDto newBookingDto = new InputBookingDto(
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(4),
                item.getId()
        );
        Mockito
                .when(mockUserRepository.findById(secondUser.getId()))
                .thenReturn(Optional.of(secondUser));
        Mockito
                .when(mockItemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        assertThrows(
                SubstanceNotFoundException.class,
                () -> bookingService.addBooking(secondUser.getId(), newBookingDto),
                "Expected addBooking() to throw SubstanceNotFoundException because the owner cannot book their item."
        );
    }

    @Test
    void handleGetBooking_withUserIsOwner() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", true, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );
        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockBookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        BookingDto actual = bookingService.getBooking(secondUser.getId(), booking.getId());

        assertEquals(expected, actual);
    }

    @Test
    void handleGetBooking_withUserIsNeitherBookerNorOwner() {
        Long randomId = 50L;
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", true, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );
        Mockito
                .when(mockBookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        assertThrows(SubstanceNotFoundException.class,
                () -> bookingService.getBooking(randomId, booking.getId()));
    }

    @Test
    void handleGetBooking_withUserIsBooker() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", true, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );
        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockBookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        BookingDto actual = bookingService.getBooking(firstUser.getId(), booking.getId());

        assertEquals(expected, actual);
    }

    @Test
    void handleGetBooking_withBookingDoesNotExist() {
        Long userId = 1L;
        Long bookingId = 51L;
        Mockito
                .when(mockBookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(
                SubstanceNotFoundException.class,
                () -> bookingService.getBooking(userId, bookingId),
                "Expected getBooking() to throw SubstanceNotFoundException because the booking does not exist."
        );
    }


    @Test
    void handleGetUserBookings_withUserDoesNotExist() {
        Long randomId = 53L;
        Mockito
                .when(mockUserRepository.findById(randomId))
                .thenReturn(Optional.empty());

        assertThrows(
                SubstanceNotFoundException.class,
                () -> bookingService.getUserBookings(randomId, BookingState.ALL, 0, 10)
        );
    }

    @Test
    void handleGetUserBookings_withStateIsWaiting() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );
        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(
                        mockBookingRepository.findBookingsByBooker_IdAndStatus(
                                firstUser.getId(),
                                BookingStatus.WAITING,
                                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"))
                        )
                )
                .thenReturn(List.of(booking));
        List<BookingDto> actual = bookingService.getUserBookings(firstUser.getId(), BookingState.WAITING, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void handleGetUserBookings_withStateIsRejected() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );

        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(
                        mockBookingRepository.findBookingsByBooker_IdAndStatus(
                                firstUser.getId(),
                                BookingStatus.REJECTED,
                                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"))
                        )
                )
                .thenReturn(List.of(booking));
        List<BookingDto> actual = bookingService.getUserBookings(firstUser.getId(), BookingState.REJECTED, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void handleGetUserBookings_withStateIsCurrent() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );

        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(
                        mockBookingRepository.findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(
                                eq(firstUser.getId()),
                                any(LocalDateTime.class),
                                any(LocalDateTime.class),
                                eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                        )
                )
                .thenReturn(List.of(booking));
        List<BookingDto> actual = bookingService.getUserBookings(firstUser.getId(), BookingState.CURRENT, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void handleGetUserBookings_withStateIsPast() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );

        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(
                        mockBookingRepository.findBookingsByBooker_IdAndEndIsBefore(
                                eq(firstUser.getId()),
                                any(LocalDateTime.class),
                                eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                        )
                )
                .thenReturn(List.of(booking));
        List<BookingDto> actual = bookingService.getUserBookings(firstUser.getId(), BookingState.PAST, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void handleGetUserBookings_withStateIsFuture() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );

        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito.when(mockUserRepository.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));
        Mockito
                .when(
                        mockBookingRepository.findBookingsByBooker_IdAndStartIsAfter(
                                eq(firstUser.getId()),
                                any(LocalDateTime.class),
                                eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                        )
                )
                .thenReturn(List.of(booking));
        List<BookingDto> actual = bookingService.getUserBookings(firstUser.getId(), BookingState.FUTURE, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void handleGetUserBookings_withStateIsAll() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );

        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(
                        mockBookingRepository.findBookingsByBooker_Id(
                                eq(firstUser.getId()),
                                eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                        )
                )
                .thenReturn(List.of(booking));
        List<BookingDto> actual = bookingService.getUserBookings(firstUser.getId(), BookingState.ALL, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void handleGetOwnerBookingList_withStateIsWaiting() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );

        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(
                        mockBookingRepository.getBookingsByUserItemsWithState(
                                firstUser.getId(),
                                BookingStatus.WAITING,
                                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"))
                        )
                )
                .thenReturn(List.of(booking));
        List<BookingDto> actual = bookingService.getOwnerBookingList(firstUser.getId(), BookingState.WAITING, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void handleGetOwnerBookingList_withStateIsRejected() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );

        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(
                        mockBookingRepository.getBookingsByUserItemsWithState(
                                firstUser.getId(),
                                BookingStatus.REJECTED,
                                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"))
                        )
                )
                .thenReturn(List.of(booking));
        List<BookingDto> actual = bookingService.getOwnerBookingList(firstUser.getId(), BookingState.REJECTED, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void handleGetOwnerBookingList_withStateIsCurrent() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );

        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(
                        mockBookingRepository.getBookingsByOwnerIdCurrent(
                                eq(firstUser.getId()),
                                eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                        )
                )
                .thenReturn(List.of(booking));
        List<BookingDto> actual = bookingService.getOwnerBookingList(firstUser.getId(), BookingState.CURRENT, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void handleGetOwnerBookingList_withStateIsPast() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );
        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(
                        mockBookingRepository.getBookingsByOwnerIdPast(
                                eq(firstUser.getId()),
                                eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                        )
                )
                .thenReturn(List.of(booking));
        List<BookingDto> actual = bookingService.getOwnerBookingList(firstUser.getId(), BookingState.PAST, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void handleGetOwnerBookingList_withStateIsFuture() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );
        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(
                        mockBookingRepository.getBookingsByOwnerIdFuture(
                                eq(firstUser.getId()),
                                eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                        )
                )
                .thenReturn(List.of(booking));
        List<BookingDto> actual = bookingService.getOwnerBookingList(firstUser.getId(), BookingState.FUTURE, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void handleGetOwnerBookingList_withStateIsAll() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );

        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(
                        mockBookingRepository.getBookingsByOwnerId(
                                eq(firstUser.getId()),
                                eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                        )
                )
                .thenReturn(List.of(booking));
        List<BookingDto> actual = bookingService.getOwnerBookingList(firstUser.getId(), BookingState.ALL, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void handleChangeBookingStatus_byDefault() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );
        BookingDto expected = new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                firstUser,
                booking.getStatus()
        );
        Mockito
                .when(mockBookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        BookingDto actual = bookingService.changeBookingStatus(secondUser.getId(), booking.getId(), true);

        assertEquals(expected, actual);
    }

    @Test
    void handleChangeBookingStatus_withBookingDoesNotExist() {
        Long bookingId = 53L;
        Mockito
                .when(mockBookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        assertThrows(
                SubstanceNotFoundException.class,
                () -> bookingService.changeBookingStatus(1L, bookingId, true)
        );
    }

    @Test
    void handleChangeBookingStatus_wihtUserIsNotOwner() {
        Long randomId = 53L;
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );
        Mockito
                .when(mockBookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        assertThrows(
                ForbiddenException.class,
                () -> bookingService.changeBookingStatus(randomId, booking.getId(), true)
        );
    }

    @Test
    void handleChangeBookingStatus_StatusIsTheSame() {
        User firstUser = new User(1L, "Adam", "adam@paradise.com");
        User secondUser = new User(2L, "Eva", "eva@paradise.com");
        Item item = new Item(3L, "Paradise", "nice garden without people", false, secondUser, null);
        Booking booking = new Booking(
                5L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                firstUser,
                BookingStatus.WAITING
        );
        Mockito
                .when(mockBookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.changeBookingStatus(secondUser.getId(), booking.getId(), true)
        );
    }


}
