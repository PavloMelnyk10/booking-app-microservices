package pavlo.melnyk.paymentservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pavlo.melnyk.paymentservice.model.Payment;
import pavlo.melnyk.paymentservice.model.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("""
    SELECT p FROM Payment p
    WHERE p.status = 'PENDING'
      AND p.createdAt < :cutoffTime
            """)
    List<Payment> findPendingBefore(@Param("cutoffTime") LocalDateTime cutoffTime);

    Page<Payment> findAllByUserId(Long id, Pageable pageable);

    Optional<Payment> findBySessionId(String sessionId);

    boolean existsByBookingIdAndStatus(Long bookingId, PaymentStatus paymentStatus);

    Optional<Payment> findByBookingId(Long id);
}
