package pavlo.melnyk.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import lombok.Data;
import pavlo.melnyk.paymentservice.model.PaymentStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDto {
    private Long id;
    private Long bookingId;
    private Long userId;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String chargeId;
    private BigDecimal amountRefunded;
}
