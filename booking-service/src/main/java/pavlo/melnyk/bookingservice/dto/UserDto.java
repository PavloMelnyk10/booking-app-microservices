package pavlo.melnyk.bookingservice.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String gender;
    private LocalDate birtDate;
    private int completedBookings;
}
