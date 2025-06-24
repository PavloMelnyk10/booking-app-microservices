package pavlo.melnyk.bookingservice.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import pavlo.melnyk.bookingservice.model.BookingStatus;

@Data
public class BookingDto {
    private Long id;
    private Long accommodationId;
    private Long userId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
    private BigDecimal price;
}
