package pavlo.melnyk.bookingservice.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pavlo.melnyk.bookingservice.exception.BookingNotFoundException;
import pavlo.melnyk.bookingservice.model.Booking;
import pavlo.melnyk.bookingservice.model.BookingStatus;
import pavlo.melnyk.bookingservice.repository.BookingRepository;

@RequiredArgsConstructor
@Component
public class Consumer {
    private final BookingRepository bookingRepository;

    @KafkaListener(topics = "payment_created_topic", groupId = "booking_group")
    public void handlePaymentCreated(PaymentEvent event) {
        Booking booking = bookingRepository.findById(event.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking with ID " + event.getBookingId() + " not found"));
        booking.setHasPayment(true);
        bookingRepository.save(booking);
    }

    @KafkaListener(topics = "booking_status_topic", groupId = "booking_group")
    public void handleBookingCancelled(PaymentEvent event) {
        Booking booking = bookingRepository.findById(event.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking with ID " + event.getBookingId() + " not found"));
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
}
