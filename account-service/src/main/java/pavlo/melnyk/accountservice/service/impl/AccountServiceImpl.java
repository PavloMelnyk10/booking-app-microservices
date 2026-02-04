package pavlo.melnyk.accountservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pavlo.melnyk.accountservice.dto.AccountDto;
import pavlo.melnyk.accountservice.mapper.AccountMapper;
import pavlo.melnyk.accountservice.model.Account;
import pavlo.melnyk.accountservice.repository.AccountRepository;
import pavlo.melnyk.accountservice.security.InternalUserPrincipal;
import pavlo.melnyk.accountservice.service.AccountService;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public void synchronizeAccount(InternalUserPrincipal principal) {
        String keycloakUserId = principal.getUserId();
        String email = principal.getEmail();

        Account account = accountRepository.findByKeycloakUserId(keycloakUserId)
                .or(() -> accountRepository.findByEmail(email))
                .orElseGet(() -> new Account().setKeycloakUserId(keycloakUserId));

        // Ensure Keycloak ID is set (in case we found by email)
        if (account.getKeycloakUserId() == null) {
            account.setKeycloakUserId(keycloakUserId);
        }

        account.setEmail(principal.getEmail());
        account.setFirstName(principal.getFirstName());
        account.setLastName(principal.getLastName());

        accountRepository.save(account);
    }

    @Override
    public AccountDto getCurrentAccount(InternalUserPrincipal principal) {
        Account account = accountRepository.findByKeycloakUserId(principal.getUserId())
                .or(() -> accountRepository.findByEmail(principal.getEmail()))
                .orElseThrow(() -> new RuntimeException("Account not found for user: " + principal.getUserId()));

        return accountMapper.toDto(account);
    }
}
