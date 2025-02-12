package pavlo.melnyk.accountservice.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pavlo.melnyk.accountservice.exception.AccountNotFoundException;
import pavlo.melnyk.accountservice.model.Account;
import pavlo.melnyk.accountservice.repository.AccountRepository;

@Component
@RequiredArgsConstructor
public class AccountKafkaConsumer {

    private final AccountRepository accountRepository;

    @KafkaListener(topics = "account_topic", groupId = "account_group")
    public void processAccountUpdate(AccountUpdateEvent message) {
        System.out.println(message.getUserId() + " " + message.getIncrement());
        Account account = accountRepository.findById(message.getUserId())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found for userId: " + message.getUserId()));

        account.setCompletedBookings(account.getCompletedBookings() + message.getIncrement());
        accountRepository.save(account);
    }
}
