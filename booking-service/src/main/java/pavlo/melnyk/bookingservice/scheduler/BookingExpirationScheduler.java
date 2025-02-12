package pavlo.melnyk.bookingservice.scheduler;

import static pavlo.melnyk.bookingservice.service.notification.notification.MessageBuilder.buildBookingExpiredMessage;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pavlo.melnyk.bookingservice.model.BookingStatus;
import pavlo.melnyk.bookingservice.repository.BookingRepository;
import pavlo.melnyk.bookingservice.service.notification.notification.KafkaNotificationProducer;

@Component
@RequiredArgsConstructor
public class BookingExpirationScheduler {
    private final BookingRepository bookingRepository;
    private final KafkaNotificationProducer notificationProducer;

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void expirePendingBookings() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);

        bookingRepository.findAllByStatusAndCreatedAtBeforeAndHasPaymentFalse(
                        BookingStatus.PENDING, cutoffTime)
                .forEach(booking -> {
                    booking.setStatus(BookingStatus.EXPIRED);
                    bookingRepository.save(booking);

                    notificationProducer.sendNotification(
                            "booking_topic",
                            buildBookingExpiredMessage(booking)
                    );
                });
    }
}
