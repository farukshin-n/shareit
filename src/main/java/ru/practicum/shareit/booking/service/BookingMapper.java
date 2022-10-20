package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            booking.getItem(),
            booking.getBooker(),
            booking.getStatus()
        );
    }

    public static Booking toBooking(InputBookingDto inputBookingDto, Item item, User user) {
        return new Booking(
                null,
                inputBookingDto.getStart(),
                inputBookingDto.getEnd(),
                item,
                user,
                BookingStatus.WAITING
        );
    }

    public static BookingDtoWithBookerId toBookingDtoWithBookerID(Booking booking) {
        return new BookingDtoWithBookerId(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.itemToDto(booking.getItem()),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }
}
