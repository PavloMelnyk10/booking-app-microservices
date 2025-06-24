package pavlo.melnyk.accommodationservice.dto.accommodation;

import java.math.BigDecimal;
import lombok.Data;
import pavlo.melnyk.accommodationservice.dto.AddressDto;
import pavlo.melnyk.accommodationservice.model.AccommodationType;

@Data
public class AccommodationSummaryDto {
    private Long id;
    private String name;
    private AccommodationType accommodationType;
    private AddressDto address;
    private String size;
    private BigDecimal dailyRate;
}
