package pavlo.melnyk.accommodationservice.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Address {
    private String country;
    private String city;
    private String street;
    private String zipCode;
    private double latitude;
    private double longitude;
}
