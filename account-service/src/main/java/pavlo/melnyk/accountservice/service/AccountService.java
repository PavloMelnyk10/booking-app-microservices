package pavlo.melnyk.accountservice.service;

import org.springframework.security.oauth2.jwt.Jwt;
import pavlo.melnyk.accountservice.dto.AccountDto;

public interface AccountService {

    void synchronizeAccount(Jwt jwt);

    AccountDto getCurrentAccount(Jwt jwt);
}

