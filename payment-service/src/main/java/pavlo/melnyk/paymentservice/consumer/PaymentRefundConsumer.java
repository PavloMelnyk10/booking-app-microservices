package pavlo.melnyk.paymentservice.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pavlo.melnyk.paymentservice.service.payment.PaymentService;

@Component
@RequiredArgsConstructor
public class PaymentRefundConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "refund_topic", groupId = "payment_group")
    public void handleRefundEvent(PaymentRefundEvent event) {
        try {
            System.out.println("Received refund event: bookingId=" + event.getBookingId()
                    + ", refundPercentage=" + event.getRefundPercentage());

            paymentService.processRefund(event.getBookingId(), event.getRefundPercentage());

        } catch (Exception e) {
            System.err.println("Error processing refund event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
