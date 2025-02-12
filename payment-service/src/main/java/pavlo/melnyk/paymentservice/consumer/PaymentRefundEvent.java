package pavlo.melnyk.paymentservice.consumer;

import lombok.Data;

@Data
public class PaymentRefundEvent {
    private Long bookingId;
    private double refundPercentage;
}
