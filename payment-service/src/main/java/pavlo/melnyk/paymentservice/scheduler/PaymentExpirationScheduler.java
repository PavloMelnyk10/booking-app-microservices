package pavlo.melnyk.paymentservice.scheduler;

import static pavlo.melnyk.paymentservice.service.notification.MessageBuilder.buildBookingExpiredDueToPaymentMessage;
import static pavlo.melnyk.paymentservice.service.notification.MessageBuilder.buildPaymentExpiredMessage;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pavlo.melnyk.paymentservice.model.PaymentStatus;
import pavlo.melnyk.paymentservice.repository.PaymentRepository;
import pavlo.melnyk.paymentservice.service.notification.KafkaNotificationProducer;
import pavlo.melnyk.paymentservice.service.payment.StripeService;

@Component
@RequiredArgsConstructor
public class PaymentExpirationScheduler {
    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;
    private final KafkaNotificationProducer notificationProducer;

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void expirePendingPayments() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(2);

        paymentRepository.findPendingBefore(cutoffTime).forEach(payment -> {
            stripeService.expireSession(payment.getSessionId());
            payment.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(payment);

            notificationProducer.sendNotification("booking_status_topic",
                    String.format("{\"bookingId\": %d}", payment.getBookingId()));

            notificationProducer.sendNotification("booking_topic",
                    buildBookingExpiredDueToPaymentMessage(payment.getBookingId()));

            notificationProducer.sendNotification("payment_topic",
                    buildPaymentExpiredMessage(payment));
        });
    }
}
