package pavlo.melnyk.bookingservice.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import pavlo.melnyk.bookingservice.validation.UpdateDateAfter;

@Data
@UpdateDateAfter(field = "checkInDate", message = "Check-out date must be after check-in date")
public class UpdateBookingRequestDto {
    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;
}
