package pavlo.melnyk.accommodationservice.dto.accommodation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import pavlo.melnyk.accommodationservice.model.AccommodationType;
import pavlo.melnyk.accommodationservice.validation.EnumValidator;

@Data
public class CreateAccommodationRequestDto {
    @NotBlank(message = "Accommodation name is required")
    @Size(min = 3, max = 100, message = "The name must be between 3 and 100 characters")
    private String name;

    @Size(min = 20, max = 500, message = "The description must be between 20 and 500 characters")
    private String description;

    @NotNull(message = "Accommodation type is required")
    @EnumValidator(enumClass = AccommodationType.class,
            message = "Invalid accommodation type. "
                    + "Allowed values: HOUSE, APARTMENT, CONDO, VACATION HOME")
    private String accommodationType;

    @NotNull(message = "Availability is required")
    @Positive(message = "Availability must be greater than zero")
    private Integer availability;

    @NotBlank(message = "Location is required")
    @Size(min = 10, max = 200, message = "The location must be between 10 and 200 characters")
    private String location;

    @NotBlank(message = "Size is required")
    @Size(min = 4, max = 50, message = "Size  must be between 4 and 50 characters")
    private String size;

    private Set<@NotBlank(message = "Amenity name cannot be blank") String> amenities;

    @NotNull(message = "Daily rate is required")
    @Positive(message = "Daily rate must be greater than zero")
    private BigDecimal dailyRate;
}
