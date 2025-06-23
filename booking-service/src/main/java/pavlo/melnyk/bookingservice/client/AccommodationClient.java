package pavlo.melnyk.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pavlo.melnyk.bookingservice.config.FeignConfig;
import pavlo.melnyk.bookingservice.dto.AccommodationAvailabilityDto;

@FeignClient(
        name = "accommodation-service",
        url = "${accommodation.service.url}",
        path = "/api/accommodation",
        configuration = FeignConfig.class
)
public interface AccommodationClient {
    @GetMapping("/availability/{id}")
    AccommodationAvailabilityDto findById(
            @PathVariable("id") Long id);
}
