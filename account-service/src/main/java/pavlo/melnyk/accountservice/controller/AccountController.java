package pavlo.melnyk.accountservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pavlo.melnyk.accountservice.dto.AccountDto;
import pavlo.melnyk.accountservice.security.InternalUserPrincipal;
import pavlo.melnyk.accountservice.service.AccountService;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/me")
    public ResponseEntity<AccountDto> getCurrentAccount(
            @AuthenticationPrincipal InternalUserPrincipal principal) {
        // Synchronize account on each request to keep data fresh
        accountService.synchronizeAccount(principal);
        return ResponseEntity.ok(accountService.getCurrentAccount(principal));
    }
}
