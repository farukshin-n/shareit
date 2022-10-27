package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // all
    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :id")
    List<Booking> getBookingsByOwnerId(Long id, Pageable pageable);

    // waiting, rejected
    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :id AND b.status = :status")
    List<Booking> getBookingsByUserItemsWithState(long id, BookingStatus status, Pageable pageable);

    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :id " +
            "AND b.start < current_timestamp AND b.end > current_timestamp ")
    List<Booking> getBookingsByOwnerIdCurrent(long id, Pageable pageable);

    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :id AND b.end < current_timestamp")
    List<Booking> getBookingsByOwnerIdPast(long id, Pageable pageable);

    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :id AND b.start > current_date")
    List<Booking> getBookingsByOwnerIdFuture(long id, Pageable pageable);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id = ? AND start_date < now() " +
            "ORDER BY end_date DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getPastOrCurrentBookingByItemId(long id);

    List<Booking> getBookingsByItem_IdInOrderByEndAsc(Set<Long> itemIds);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id = ? AND start_date > now() " +
            "ORDER BY start_date " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getFutureBookingByItemId(long id);

    // all
    List<Booking> findBookingsByBooker_Id(long id, Pageable pageable);

    // current
    List<Booking> findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(long id,
                                                                       LocalDateTime checkStart,
                                                                       LocalDateTime checkEnd,
                                                                       Pageable pageable);

    // past
    List<Booking> findBookingsByBooker_IdAndEndIsBefore(long id,
                                                        LocalDateTime dateTime,
                                                        Pageable pageable);

    // future
    List<Booking> findBookingsByBooker_IdAndStartIsAfter(long id, LocalDateTime dateTime, Pageable pageable);

    // waiting & rejected

    List<Booking> findBookingsByBooker_IdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findBookingsByItem_IdAndBooker_IdAndEndIsBefore(long itemId,
                                                                  long bookerId,
                                                                  LocalDateTime dateTime);
}
