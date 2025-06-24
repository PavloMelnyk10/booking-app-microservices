package pavlo.melnyk.bookingservice.service.booking;

import static pavlo.melnyk.bookingservice.service.notification.notification.MessageBuilder.buildBookingCancelledMessage;
import static pavlo.melnyk.bookingservice.service.notification.notification.MessageBuilder.buildBookingCreatedMessage;
import static pavlo.melnyk.bookingservice.service.notification.notification.MessageBuilder.buildBookingRefundMessage;
import static pavlo.melnyk.bookingservice.service.notification.notification.MessageBuilder.buildBookingUpdatedMessage;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import pavlo.melnyk.bookingservice.client.AccommodationClient;
import pavlo.melnyk.bookingservice.client.AccountClient;
import pavlo.melnyk.bookingservice.dto.AccommodationDto;
import pavlo.melnyk.bookingservice.dto.UserDto;
import pavlo.melnyk.bookingservice.dto.booking.BookingDto;
import pavlo.melnyk.bookingservice.dto.booking.CreateBookingRequestDto;
import pavlo.melnyk.bookingservice.dto.booking.UpdateBookingRequestDto;
import pavlo.melnyk.bookingservice.exception.AccessDeniedException;
import pavlo.melnyk.bookingservice.exception.AccommodationFullyBookedException;
import pavlo.melnyk.bookingservice.exception.AccommodationNotFoundException;
import pavlo.melnyk.bookingservice.exception.BookingNotFoundException;
import pavlo.melnyk.bookingservice.mapper.BookingMapper;
import pavlo.melnyk.bookingservice.model.Booking;
import pavlo.melnyk.bookingservice.model.BookingStatus;
import pavlo.melnyk.bookingservice.repository.BookingRepository;
import pavlo.melnyk.bookingservice.service.discount.DiscountStrategy;
import pavlo.melnyk.bookingservice.service.discount.DiscountStrategyFactory;
import pavlo.melnyk.bookingservice.service.notification.notification.KafkaNotificationProducer;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final AccountClient accountClient;
    private final AccommodationClient accommodationClient;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final KafkaNotificationProducer notificationProducer;
    private final DiscountStrategyFactory discountStrategyFactory;

    @Override
    public BookingDto createBooking(CreateBookingRequestDto requestDto) {
        UserDto currentUser = accountClient.getCurrentUser();

        AccommodationDto accommodation
                = getAccommodationOrThrow(requestDto.getAccommodationId());

        checkBookingOverlap(
                accommodation.getId(), requestDto.getCheckInDate(),
                requestDto.getCheckOutDate(), null);

        Booking booking = createNewBooking(requestDto, currentUser, accommodation);

        Booking savedBooking = bookingRepository.save(booking);

        notificationProducer.sendNotification(
                "booking_topic", buildBookingCreatedMessage(savedBooking));

        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public Page<BookingDto> getCurrentUserBookings(Pageable pageable) {
        UserDto currentUser = accountClient.getCurrentUser();
        Page<Booking> userBookings = bookingRepository
                .findAllByUserId(currentUser.getId(), pageable);
        return userBookings.map(bookingMapper::toDto);
    }

    @Override
    public Page<BookingDto> findByUserIdAndStatus(final Long userId,
                                                  final BookingStatus status,
                                                  final Pageable pageable) {
        return bookingRepository.findAllByUserIdAndStatus(
                userId, status, pageable).map(bookingMapper::toDto);
    }

    @Override
    public BookingDto findBookingById(final Long id) {
        Booking booking = getBookingOrThrow(id);
        validateBookingOwnership(id);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto updateBooking(final Long bookingId,
                                    final UpdateBookingRequestDto requestDto) {
        validateBookingOwnership(bookingId);
        Booking booking = getBookingOrThrow(bookingId);
        validateBookingStatusForUpdate(booking);

        checkBookingOverlap(booking.getAccommodationId(),
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate(),
                bookingId);

        updateBookingDetails(booking, requestDto);
        Booking updatedBooking = bookingRepository.save(booking);

        notificationProducer.sendNotification(
                "booking_topic", buildBookingUpdatedMessage(updatedBooking, requestDto));

        return bookingMapper.toDto(updatedBooking);
    }

    @Override
    public void cancelBookingById(final Long bookingId) {
        validateBookingOwnership(bookingId);
        Booking booking = getBookingOrThrow(bookingId);

        validateBookingStatusForCancellation(booking);

        double refundPercentage = calculateRefundPercentage(
                booking.getCheckInDate(), LocalDate.now());

        notificationProducer.sendNotification(
                "refund_topic", buildBookingRefundMessage(booking, refundPercentage));

        booking.setStatus(BookingStatus.CANCELLED);

        notificationProducer.sendNotification(
                "booking_topic", buildBookingCancelledMessage(booking));

        bookingRepository.save(booking);
    }

    @Override
    public void changeStatus(Long bookingId, BookingStatus status) {
        validateBookingOwnership(bookingId);
        Booking booking = getBookingOrThrow(bookingId);
        booking.setStatus(status);
        bookingRepository.save(booking);
    }

    private AccommodationDto getAccommodationOrThrow(Long accommodationId) {
        try {
            return accommodationClient.findById(accommodationId);
        } catch (Exception e) {
            throw new AccommodationNotFoundException(
                    "Accommodation with id " + accommodationId + " not found"
            );
        }
    }

    private Booking createNewBooking(CreateBookingRequestDto requestDto,
                                     UserDto currentUser,
                                     AccommodationDto accommodation) {
        Booking booking = bookingMapper.toModel(requestDto);
        booking.setUserId(currentUser.getId());
        booking.setAccommodationId(accommodation.getId());
        booking.setStatus(BookingStatus.PENDING);

        long completedBookings = currentUser.getCompletedBookings();

        DiscountStrategy discountStrategy = discountStrategyFactory.getStrategy(
                (int) completedBookings
        );

        BigDecimal originalPrice = accommodation.getDailyRate();
        BigDecimal discount = discountStrategy.calculateDiscount(
                originalPrice, (int) completedBookings
        );
        booking.setPrice(originalPrice.subtract(discount));
        return booking;
    }

    private void validateBookingStatusForUpdate(Booking booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED
                || booking.getStatus() == BookingStatus.EXPIRED
                || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Cannot update a booking with status " + booking.getStatus());
        }
    }

    private void updateBookingDetails(Booking booking, UpdateBookingRequestDto requestDto) {
        booking.setCheckInDate(requestDto.getCheckInDate());
        booking.setCheckOutDate(requestDto.getCheckOutDate());
    }

    private void validateBookingStatusForCancellation(Booking booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED
                || booking.getStatus() == BookingStatus.EXPIRED
                || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Booking cannot be canceled as it is already " + booking.getStatus());
        }

        if (booking.getStatus() == BookingStatus.PENDING) {
            Duration timeLeft = Duration.between(LocalDateTime.now(),
                    booking.getCreatedAt().plusMinutes(15));
            throw new IllegalStateException(
                    "This booking is still pending payment. It will be automatically cancelled in "
                            + timeLeft.toMinutes() + " minutes if no payment is made.");
        }
    }

    private double calculateRefundPercentage(LocalDate checkInDate, LocalDate cancellationDate) {
        long daysBetween = ChronoUnit.DAYS.between(cancellationDate, checkInDate);
        if (daysBetween >= 7) {
            return 1.0;
        } else if (daysBetween >= 3) {
            return 0.5;
        } else {
            return 0.0;
        }
    }

    private void checkBookingOverlap(final Long accommodationId,
                                   final LocalDate checkIn,
                                   final LocalDate checkOut,
                                   final Long excludeBookingId) {
        long overlappingCount = (excludeBookingId == null)
                ? bookingRepository.countOverlappingBookings(
                accommodationId, checkIn, checkOut)
                : bookingRepository.countOverlappingBookings(
                accommodationId, excludeBookingId, checkIn, checkOut);

        if (overlappingCount >= 1) {
            throw new AccommodationFullyBookedException(
                    "Accommodation is fully booked for the given dates");
        }
    }

    private Booking getBookingOrThrow(final Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(()
                -> new BookingNotFoundException("Booking with ID " + bookingId + " not found"));
    }

    private void validateBookingOwnership(final Long bookingId) {
        UserDto currentUser = accountClient.getCurrentUser();
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new BookingNotFoundException("Booking with ID " + bookingId + " not found"));

        if (currentUserHasAdminRole((Jwt) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal())) {
            return;
        }

        if (!booking.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access this booking");
        }
    }

    private boolean currentUserHasAdminRole(Jwt jwt) {
        var roles = jwt.getClaimAsStringList("spring_sec_roles");
        return roles != null && roles.contains("ROLE_ADMIN");
    }
}
