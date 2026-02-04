package pavlo.melnyk.accountservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pavlo.melnyk.accountservice.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByKeycloakUserId(String userId);

    Optional<Account> findByEmail(String email);

    boolean existsByKeycloakUserId(String userId);
}
