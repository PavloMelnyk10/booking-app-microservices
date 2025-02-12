package pavlo.melnyk.bookingservice.scheduler;

import static pavlo.melnyk.bookingservice.service.notification.notification.MessageBuilder.buildBookingCompletedForAccountMessage;
import static pavlo.melnyk.bookingservice.service.notification.notification.MessageBuilder.buildBookingCompletedMessage;

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
public class BookingCompletionScheduler {
    private final BookingRepository bookingRepository;
    private final KafkaNotificationProducer kafkaNotificationProducer;

    @Scheduled(cron = "0 0 17 * * ?")
    @Transactional
    public void completeBookings() {
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();

        bookingRepository.findAllByStatusAndCheckOutDate(
                BookingStatus.CONFIRMED, today.toLocalDate())
                .forEach(booking -> {
                    booking.setStatus(BookingStatus.COMPLETED);
                    bookingRepository.save(booking);

                    kafkaNotificationProducer.sendNotification(
                            "booking_topic",
                            buildBookingCompletedMessage(booking)
                    );

                    kafkaNotificationProducer.sendNotification(
                            "account_topic",
                            buildBookingCompletedForAccountMessage(booking)
                    );
                });
    }
}
