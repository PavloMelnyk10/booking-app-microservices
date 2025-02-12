package pavlo.melnyk.bookingservice.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pavlo.melnyk.bookingservice.model.Booking;
import pavlo.melnyk.bookingservice.model.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT COUNT(b) FROM Booking b "
            + "WHERE b.accommodationId = :accommodationId "
            + "AND b.status IN ('PENDING', 'CONFIRMED') "
            + "AND b.checkInDate < :checkOutDate "
            + "AND b.checkOutDate > :checkInDate")
    long countOverlappingBookings(
            Long accommodationId,
            LocalDate checkInDate,
            LocalDate checkOutDate);

    @Query("SELECT COUNT(b) FROM Booking b "
            + "WHERE b.accommodationId = :accommodationId "
            + "AND b.status IN ('PENDING', 'CONFIRMED') "
            + "AND b.id != :bookingId "
            + "AND b.checkInDate < :checkOutDate "
            + "AND b.checkOutDate > :checkInDate")
    long countOverlappingBookings(Long accommodationId,
                                  Long bookingId,
                                  LocalDate checkInDate,
                                  LocalDate checkOutDate);

    Page<Booking> findAllByUserId(Long userId, Pageable pageable);

    Page<Booking> findAllByUserIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByStatusAndCheckOutDate(BookingStatus bookingStatus,
                                                 LocalDate checkOutDate);

    List<Booking> findAllByStatusAndCreatedAtBeforeAndHasPaymentFalse(
            BookingStatus status,
            LocalDateTime cutoffTime);
}
