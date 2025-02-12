package pavlo.melnyk.bookingservice.service.booking;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pavlo.melnyk.bookingservice.dto.booking.BookingDto;
import pavlo.melnyk.bookingservice.dto.booking.CreateBookingRequestDto;
import pavlo.melnyk.bookingservice.dto.booking.UpdateBookingRequestDto;
import pavlo.melnyk.bookingservice.model.BookingStatus;

public interface BookingService {
    BookingDto createBooking(CreateBookingRequestDto requestDto);

    Page<BookingDto> getCurrentUserBookings(Pageable pageable);

    Page<BookingDto> findByUserIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    BookingDto updateBooking(Long id, @Valid UpdateBookingRequestDto requestDto);

    BookingDto findBookingById(Long id);

    void cancelBookingById(Long id);

    void changeStatus(Long bookingId, BookingStatus status);
}
