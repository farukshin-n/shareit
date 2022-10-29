package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(long userId, InputBookingDto inputBookingDto);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getUserBookings(long userId, BookingState state, int from, int size);

    BookingDto changeBookingStatus(long userId, long bookingId, boolean changeStatus);

    List<BookingDto> getOwnerBookingList(long ownerId, BookingState state, int from, int size);
}
