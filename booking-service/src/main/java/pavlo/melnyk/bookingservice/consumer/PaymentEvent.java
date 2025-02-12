package pavlo.melnyk.bookingservice.consumer;

import lombok.Data;

@Data
public class PaymentEvent {
    private Long bookingId;
}
