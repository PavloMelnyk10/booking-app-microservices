package pavlo.melnyk.accountservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pavlo.melnyk.accountservice.dto.AccountDto;
import pavlo.melnyk.accountservice.mapper.AccountMapper;
import pavlo.melnyk.accountservice.model.Account;
import pavlo.melnyk.accountservice.repository.AccountRepository;
import pavlo.melnyk.accountservice.service.AccountService;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public void synchronizeAccount(Jwt jwt) {
        String keycloakUserId = jwt.getClaimAsString("sub");
        Account account = accountRepository.findByKeycloakUserId(keycloakUserId)
                .orElseGet(() -> new Account().setKeycloakUserId(keycloakUserId));

        account.setEmail(jwt.getClaimAsString("email"));
        account.setFirstName(jwt.getClaimAsString("given_name"));
        account.setLastName(jwt.getClaimAsString("family_name"));

        accountRepository.save(account);
    }

    @Override
    public AccountDto getCurrentAccount(Jwt jwt) {
        Account account = accountRepository
                .findByKeycloakUserId(jwt.getClaimAsString("sub")).orElseThrow();

        return accountMapper.toDto(account);
    }
}
