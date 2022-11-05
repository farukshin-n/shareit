package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.InputBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
										   @RequestBody @Valid InputBookingDto inputBookingDto) {
		validateStartEndOfBooking(inputBookingDto);
		log.info("Creating booking {}, userId={}", inputBookingDto, userId);
		return bookingClient.bookItem(userId, inputBookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> patchBooking(@RequestHeader("X-Sharer-User-Id") long id,
											@PathVariable long bookingId,
											@RequestParam boolean approved) {
		log.info("We have request for setting status {} by user with id {} for item {}.",
				approved,
				id,
				bookingId);
		return bookingClient.patchItem(id, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long id,
		@RequestParam(name = "state", defaultValue = "all") String stateParam,
		@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
		@Positive @RequestParam(name = "size", defaultValue = "10") int size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get request for bookings with state {}, userId={}, from={}, size={}", stateParam, id, from, size);
		return bookingClient.getBookings(id, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
												   @RequestParam(name = "state", defaultValue = "ALL")
												   String stateParam,
												   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
												   Integer from,
												   @Positive @RequestParam(name = "size", defaultValue = "10")
												   Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get request for bookings by owner with state {}, by userId={}, from={}, size={}",
				stateParam, userId, from, size);
		return bookingClient.getOwnerBookings(userId, state, from, size);
	}

	private void validateStartEndOfBooking(InputBookingDto inputBookingDto) {
		if (inputBookingDto.getStart().isAfter(inputBookingDto.getEnd())) {
			throw new IllegalArgumentException("Start of booking cannot ba after its end.");
		}
	}
}
