package pavlo.melnyk.paymentservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingDto {
    private Long id;
    private Long accommodationId;
    private Long userId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
    private BigDecimal dailyRate;
}
