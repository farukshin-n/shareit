package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long id,
                                 @Valid @RequestBody InputBookingDto inputBookingDto) {
        log.info("We have request for adding booking {} by user with id {}.", inputBookingDto, id);
        return service.addBooking(id, inputBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatusOfBooking(@RequestHeader("X-Sharer-User-Id") long id,
                                            @PathVariable long bookingId,
                                            @RequestParam boolean approved) {
        log.info("We have request for setting status {} by user with id {} for item {}.",
                approved,
                id,
                bookingId);
        return service.changeBookingStatus(id, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long id,
                                 @PathVariable long bookingId) {
        return service.getBooking(id, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") long id,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "100") @Positive int size) {
        return service.getUserBookings(id, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getUserBookedItems(
            @RequestHeader("X-Sharer-User-Id") long id,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "100") @Positive int size) {
        return service.getOwnerBookingList(id, state, from, size);
    }
}
