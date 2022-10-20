package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto addBooking(long userId, InputBookingDto inputBookingDto) {
        validateStartEndOfBooking(inputBookingDto);
        final User booker = userRepository.findById(userId).orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't user with id %d in database.", userId)));
        Item bookingItem = itemRepository.findById(inputBookingDto.getItemId())
                .orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't item with id %d in database.", inputBookingDto.getItemId())
        ));
        if (!bookingItem.isAvailable()) {
            throw new NotAvailableException(
                    String.format("Item with id %d is not available for booking.", inputBookingDto.getItemId()));
        }
        if (booker.getId().equals(bookingItem.getOwner().getId())) {
            throw new SubstanceNotFoundException(String.format(
                    "User with id %d cannot add item with %d", booker.getId(), bookingItem.getId()));
        }
        final Booking savedBooking = bookingRepository.save(BookingMapper
                .toBooking(inputBookingDto, bookingItem, booker));
        log.info("New booking saved successfully.");

        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new SubstanceNotFoundException(
                        String.format("There isn't booking with id %d in database.", bookingId)));
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new SubstanceNotFoundException(
                        String.format("There isn't user with id %d in database.", userId)));
        if (!Objects.equals(user.getId(), booking.getBooker().getId())
                && !Objects.equals(user.getId(), booking.getItem().getOwner().getId())) {
            throw new SubstanceNotFoundException(String.format(
                            "User with id %d cannot get item with %d", userId, bookingId)
            );
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, BookingState state) {
        userRepository.findById(userId).orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't user with id %d in database.", userId)));
        List<Booking> resultList;

        switch (state) {
            case WAITING:
                resultList = bookingRepository.findBookingsByBookerIdAndStatus(
                        userId,
                        BookingState.WAITING,
                        Sort.by(Sort.Direction.DESC, "start")
                );
                break;
            case REJECTED:
                resultList = bookingRepository.findBookingsByBookerIdAndStatus(
                        userId,
                        BookingState.REJECTED,
                        Sort.by(Sort.Direction.DESC, "start")
                );
                break;
            case CURRENT:
                resultList = bookingRepository.findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );
                break;
            case PAST:
                resultList = bookingRepository.findBookingsByBooker_IdAndEndIsBefore(
                        userId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start")
                );
                break;
            case FUTURE:
                resultList = bookingRepository.findBookingsByBooker_IdAndStartIsAfter(
                        userId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start")
                );
                break;
            case ALL:
                resultList = bookingRepository.findBookingsByBooker_Id(
                        userId,
                        Sort.by(Sort.Direction.DESC, "start")
                );
                break;
            default:
                throw new NotAvailableException("Unknown state: " + state);
        }

        return resultList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getUserBookedItems(long userId, BookingState state) {
        userRepository.findById(userId).orElseThrow(() -> new SubstanceNotFoundException(
                String.format("There isn't user with id %d in database.", userId)));
        List<Booking> resultList;

        switch (state) {
            case WAITING:
                resultList = bookingRepository.getBookingsByUserItemsWithState(
                        userId,
                        BookingStatus.WAITING,
                        Sort.by(Sort.Direction.DESC, "start")
                );
                break;
            case REJECTED:
                resultList = bookingRepository.getBookingsByUserItemsWithState(
                        userId,
                        BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.DESC, "start")
                );
                break;
            case CURRENT:
                resultList = bookingRepository.getBookingsByOwnerIdCurrent(
                        userId,
                        Sort.by(Sort.Direction.DESC, "start")
                );
                break;
            case PAST:
                resultList = bookingRepository.getBookingsByOwnerIdPast(
                        userId,
                        Sort.by(Sort.Direction.DESC, "start")
                );
                break;
            case FUTURE:
                resultList = bookingRepository.getBookingsByOwnerIdFuture(
                        userId,
                        Sort.by(Sort.Direction.DESC, "start")
                );
                break;
            case ALL:
                resultList = bookingRepository.getBookingsByOwnerId(
                        userId,
                        Sort.by(Sort.Direction.DESC, "start")
                );
                break;
            default:
                throw new NotAvailableException("Unknown state: " + state);
        }
        return resultList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto changeBookingStatus(long userId, long bookingId, boolean changeStatus) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new SubstanceNotFoundException(
                        String.format("There isn't booking with id %d in database.", bookingId)));
        if (userId != booking.getItem().getOwner().getId()) {
            throw new ForbiddenException(
                    String.format("User with id %d cannot get item with %d", userId, bookingId));
        }
        BookingStatus status;
        if (changeStatus) {
            status = BookingStatus.APPROVED;
        } else {
            status = BookingStatus.REJECTED;
        }
        if (booking.getStatus() == status) {
            throw new IllegalArgumentException();
        }
        booking.setStatus(status);
        Booking newBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(newBooking);
    }

    private void validateStartEndOfBooking(InputBookingDto inputBookingDto) {
        if (inputBookingDto.getStart().isAfter(inputBookingDto.getEnd())) {
            throw new IllegalStartEndOfBookingException("Start of booking cannot ba after its end.");
        }
    }
}
