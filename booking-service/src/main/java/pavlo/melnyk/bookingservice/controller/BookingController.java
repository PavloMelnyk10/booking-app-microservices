package pavlo.melnyk.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pavlo.melnyk.bookingservice.dto.booking.BookingDto;
import pavlo.melnyk.bookingservice.dto.booking.CreateBookingRequestDto;
import pavlo.melnyk.bookingservice.dto.booking.UpdateBookingRequestDto;
import pavlo.melnyk.bookingservice.model.BookingStatus;
import pavlo.melnyk.bookingservice.service.booking.BookingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Create a new booking",
            description = "Allows users to create a new accommodation booking")
    public BookingDto createBooking(@RequestBody @Valid CreateBookingRequestDto requestDto) {
        return bookingService.createBooking(requestDto);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get current user's bookings",
            description = "Retrieves all bookings for the currently logged-in user")
    public ResponseEntity<Page<BookingDto>> getCurrentUserBookings(
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(bookingService.getCurrentUserBookings(pageable));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get bookings by user and status",
            description = "Retrieves bookings filtered by user ID "
                    + "and booking status (Admins-only endpoint)")
    public ResponseEntity<Page<BookingDto>> getBookingsByUserAndStatus(
            @RequestParam("userId") Long userId, @RequestParam("status") BookingStatus status,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(bookingService.findByUserIdAndStatus(userId, status, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get booking details",
            description = "Provides detailed information about a specific booking")
    public BookingDto getBookingById(@PathVariable("id") Long id) {
        return bookingService.findBookingById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Update booking details",
            description = "Allows users to update details of an existing booking")
    public BookingDto updateBooking(@PathVariable("id") Long id,
                                    @RequestBody @Valid UpdateBookingRequestDto requestDto) {
        return bookingService.updateBooking(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancel a booking",
            description = "Allows users to cancel an existing booking")
    public void cancelBooking(@PathVariable("id") Long id) {
        bookingService.cancelBookingById(id);
    }

    @PostMapping("/change-booking-status")
    public ResponseEntity<Void> completeBooking(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam("status") BookingStatus status) {
        bookingService.changeStatus(bookingId, status);
        return ResponseEntity.ok().build();
    }
}
