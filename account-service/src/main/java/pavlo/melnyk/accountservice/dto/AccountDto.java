package pavlo.melnyk.accountservice.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AccountDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String gender;
    private LocalDate birthDate;
    private int completedBookings;
}
