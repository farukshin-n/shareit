package ru.practicum.shareit.booking.model;

import lombok.Value;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Value
public class Booking {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    @NotNull(message = "Booking cannot be null")
    Item item;
    @NotNull(message = "Booker cannot be null")
    User booker;
    @NotNull(message = "Item status cannot be null")
    BookingStatus status;
}
