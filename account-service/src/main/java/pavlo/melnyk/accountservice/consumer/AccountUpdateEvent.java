package pavlo.melnyk.accountservice.consumer;

import lombok.Data;

@Data
public class AccountUpdateEvent {
    private Long userId;
    private int increment;
}
