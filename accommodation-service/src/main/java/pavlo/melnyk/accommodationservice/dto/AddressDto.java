package pavlo.melnyk.accommodationservice.dto;

import lombok.Data;

@Data
public class AddressDto {
    private String country;
    private String city;
    private String street;
    private String zipCode;
    private double latitude;
    private double longitude;
}
