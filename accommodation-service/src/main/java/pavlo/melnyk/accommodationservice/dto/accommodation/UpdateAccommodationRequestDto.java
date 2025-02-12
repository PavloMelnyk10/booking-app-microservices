package pavlo.melnyk.accommodationservice.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import pavlo.melnyk.accommodationservice.model.AccommodationType;
import pavlo.melnyk.accommodationservice.validation.EnumValidator;

@Data
public class UpdateAccommodationRequestDto {
    @Size(min = 3, max = 100, message = "The name must be between 3 and 100 characters")
    private String name;

    @Size(min = 20, max = 500, message = "The description must be between 20 and 500 characters")
    private String description;

    @EnumValidator(enumClass = AccommodationType.class,
            message = "Invalid accommodation type. "
                    + "Allowed values: HOUSE, APARTMENT, CONDO, VACATION HOME")
    private String accommodationType;

    @PositiveOrZero(message = "Availability must be zero or a positive number")
    private Integer availability;

    @Size(min = 10, max = 200, message = "The location must be between 10 and 200 characters")
    private String location;

    @Size(min = 4, max = 50, message = "Size  must be between 4 and 50 characters")
    private String size;

    private Set<@NotBlank(message = "Amenity name cannot be blank") String> amenities;

    @Positive(message = "Daily rate must be greater than zero")
    private BigDecimal dailyRate;
}
