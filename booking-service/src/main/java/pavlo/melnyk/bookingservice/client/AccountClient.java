package pavlo.melnyk.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import pavlo.melnyk.bookingservice.dto.UserDto;

@FeignClient(
        name = "account-service",
        url = "${account.service.url}",
        path = "/api/account")
public interface AccountClient {
    @GetMapping(value = "/me", produces = "application/json")
    UserDto getCurrentUser();
}
