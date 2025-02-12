package pavlo.melnyk.accountservice.dto;

import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.oauth2.jwt.Jwt;
import pavlo.melnyk.accountservice.model.Account;

@Data
@Accessors(chain = true)
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

    public static UserDto userDtoBuilder(Jwt jwt, Account account) {
        return new UserDto()
                .setEmail(jwt.getClaimAsString("email"))
                .setFirstName(jwt.getClaimAsString("given_name"))
                .setLastName(jwt.getClaimAsString("family_name"))
                .setId(account.getId())
                .setAddress(account.getAddress())
                .setPhoneNumber(account.getPhoneNumber())
                .setGender(account.getGender())
                .setBirtDate(account.getBirthDate())
                .setCompletedBookings(account.getCompletedBookings());
    }
}
