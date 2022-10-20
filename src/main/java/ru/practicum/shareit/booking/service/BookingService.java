package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(long userId, InputBookingDto inputBookingDto);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getUserBookings(long userId, BookingState state);

    List<BookingDto> getUserBookedItems(long userId, BookingState state);

    BookingDto changeBookingStatus(long userId, long bookingId, boolean changeStatus);
}
