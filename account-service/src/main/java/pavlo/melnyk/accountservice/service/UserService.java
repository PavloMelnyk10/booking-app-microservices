package pavlo.melnyk.accountservice.service;

import static pavlo.melnyk.accountservice.dto.UserDto.userDtoBuilder;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import pavlo.melnyk.accountservice.dto.UserDto;
import pavlo.melnyk.accountservice.model.Account;
import pavlo.melnyk.accountservice.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AccountRepository accountRepository;

    public UserDto getCurrentUser(Jwt jwt) {
        Account account = accountRepository
                .findByKeycloakUserId(jwt.getClaimAsString("sub")).orElseThrow();

        return userDtoBuilder(jwt, account);
    }
}
