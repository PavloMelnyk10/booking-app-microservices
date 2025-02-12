package pavlo.melnyk.accommodationservice.dto.accommodation;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import pavlo.melnyk.accommodationservice.model.AccommodationType;

@Data
public class AccommodationDto {
    private Long id;
    private String name;
    private String description;
    private AccommodationType accommodationType;
    private Integer availability;
    private String location;
    private String size;
    private Set<String> amenities;
    private BigDecimal dailyRate;
}
