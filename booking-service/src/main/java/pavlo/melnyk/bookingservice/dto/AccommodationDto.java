package pavlo.melnyk.bookingservice.dto;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;

@Data
public class AccommodationDto {
    private Long id;
    private String name;
    private String description;
    private String accommodationType;
    private AddressDto address;
    private String size;
    private Set<String> amenities;
    private BigDecimal dailyRate;
}
