package pavlo.melnyk.paymentservice.service.payment;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pavlo.melnyk.paymentservice.dto.BookingDto;
import pavlo.melnyk.paymentservice.dto.BookingStatus;
import pavlo.melnyk.paymentservice.exception.EntityNotFoundException;
import pavlo.melnyk.paymentservice.model.Payment;
import pavlo.melnyk.paymentservice.model.PaymentStatus;
import pavlo.melnyk.paymentservice.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentValidationService {
    private final PaymentRepository paymentRepository;

    public Payment getPaymentOrThrow(final String sessionId) {
        return paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for session: " + sessionId));
    }

    public Payment getPaymentOrThrow(final Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for booking: " + bookingId));
    }

    public void validateBookingStatus(final BookingDto booking) {
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Cannot create payment for booking with status " + booking.getStatus());
        }
    }

    public void validatePendingPaymentsForBooking(final Long bookingId) {
        boolean hasPendingPayments
                = paymentRepository.existsByBookingIdAndStatus(bookingId, PaymentStatus.PENDING);
        if (hasPendingPayments) {
            throw new IllegalArgumentException(
                    "A pending payment already exists for this booking.");
        }
    }

    public BigDecimal calculateTotalPrice(final BookingDto booking) {
        validateBookingDates(booking);
        validateDailyRate(booking);

        long days = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        if (days <= 0) {
            throw new IllegalArgumentException(
                    "Invalid booking dates: check-out must be after check-in");
        }

        return booking.getPrice().multiply(BigDecimal.valueOf(days));
    }

    private void validateBookingDates(final BookingDto booking) {
        if (booking.getCheckInDate() == null || booking.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Booking dates are not specified");
        }
    }

    private void validateDailyRate(final BookingDto booking) {
        if (booking.getPrice() == null) {
            throw new IllegalArgumentException("Accommodation daily rate is not set");
        }
    }
}
