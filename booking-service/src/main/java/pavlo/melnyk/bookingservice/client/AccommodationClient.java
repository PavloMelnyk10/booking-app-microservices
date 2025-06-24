package pavlo.melnyk.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pavlo.melnyk.bookingservice.config.FeignConfig;
import pavlo.melnyk.bookingservice.dto.AccommodationDto;

@FeignClient(
        name = "accommodation-service",
        url = "${accommodation.service.url}",
        path = "/api/accommodation",
        configuration = FeignConfig.class
)
public interface AccommodationClient {
    @GetMapping("/{id}")
    AccommodationDto findById(
            @PathVariable("id") Long id);
}
