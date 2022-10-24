package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // all
    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :id")
    List<Booking> getBookingsByOwnerId(Long id, Sort sort);

    // waiting, rejected
    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :id AND b.status = :status")
    List<Booking> getBookingsByUserItemsWithState(long id, BookingStatus status, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :id " +
            "AND b.start < current_timestamp AND b.end > current_timestamp ")
    List<Booking> getBookingsByOwnerIdCurrent(long id, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :id AND b.end < current_timestamp")
    List<Booking> getBookingsByOwnerIdPast(long id, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :id AND b.start > current_date")
    List<Booking> getBookingsByOwnerIdFuture(long id, Sort sort);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id = ? AND start_date < now() " +
            "ORDER BY end_date DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getPastOrCurrentBookingByItemId(long id);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id IN ? AND start_date < now() " +
            "ORDER BY end_date DESC " +
            "LIMIT 1",
            nativeQuery = true)
    List<Optional<Booking>> getPastOrCurrentBookingByItemIdIn(Set<Long> itemIds);

    List<Booking> getBookingsByItem_IdInOrderByEndAsc(Set<Long> itemIds);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id = ? AND start_date > now() " +
            "ORDER BY start_date " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getFutureBookingByItemId(long id);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE item_id IN ? AND start_date > now() " +
            "ORDER BY start_date " +
            "LIMIT 1",
            nativeQuery = true)
    List<Optional<Booking>> getFutureBookingByItemIdList(Set<Long> itemIds);

    // all
    List<Booking> findBookingsByBooker_Id(long id, Sort sort);

    // current
    List<Booking> findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(long id,
                                                                       LocalDateTime checkStart,
                                                                       LocalDateTime checkEnd);

    // past
    List<Booking> findBookingsByBooker_IdAndEndIsBefore(long id, LocalDateTime dateTime, Sort sort);

    // future
    List<Booking> findBookingsByBooker_IdAndStartIsAfter(long id, LocalDateTime dateTime, Sort sort);

    // waiting & rejected

    List<Booking> findBookingsByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findBookingsByItem_IdAndBooker_IdAndEndIsBefore(long itemId,
                                                                  long bookerId,
                                                                  LocalDateTime dateTime);
}
