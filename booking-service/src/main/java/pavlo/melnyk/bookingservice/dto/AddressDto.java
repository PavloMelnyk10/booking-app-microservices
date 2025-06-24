package pavlo.melnyk.bookingservice.dto;

import lombok.Data;

@Data
public class AddressDto {
    private String country;
    private String city;
    private String street;
    private String zipCode;
    private Double latitude;
    private Double longitude;
}
