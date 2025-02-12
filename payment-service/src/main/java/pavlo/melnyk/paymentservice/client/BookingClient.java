package pavlo.melnyk.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pavlo.melnyk.paymentservice.dto.BookingDto;
import pavlo.melnyk.paymentservice.dto.BookingStatus;

@FeignClient(
        name = "booking-service",
        url = "${booking.service.url}",
        path = "/api/booking"
)
public interface BookingClient {
    @GetMapping("/{id}")
    BookingDto getBookingById(@PathVariable("id") Long id);

    @PostMapping("/change-booking-status")
    void changeStatus(@RequestParam("bookingId") Long bookingId,
                      @RequestParam("status") BookingStatus status);
}

