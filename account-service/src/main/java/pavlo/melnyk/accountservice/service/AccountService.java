package pavlo.melnyk.accountservice.service;

import pavlo.melnyk.accountservice.dto.AccountDto;
import pavlo.melnyk.accountservice.security.InternalUserPrincipal;

public interface AccountService {

    void synchronizeAccount(InternalUserPrincipal principal);

    AccountDto getCurrentAccount(InternalUserPrincipal principal);
}
