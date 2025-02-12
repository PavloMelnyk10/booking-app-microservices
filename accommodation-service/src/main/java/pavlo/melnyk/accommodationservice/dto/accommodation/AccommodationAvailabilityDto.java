package pavlo.melnyk.accommodationservice.dto.accommodation;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class AccommodationAvailabilityDto {
    private Long id;
    private Integer availability;
    private BigDecimal dailyRate;
}
