package pavlo.melnyk.bookingservice.service.notification.notification;

import pavlo.melnyk.bookingservice.dto.booking.UpdateBookingRequestDto;
import pavlo.melnyk.bookingservice.model.Booking;

public final class MessageBuilder {
    private MessageBuilder() {
    }

    /* Booking */
    public static String buildBookingCreatedMessage(Booking booking) {
        return String.format(
                """
                🛏️ New Booking:
                ID: %d
                Check-in Date: %s
                Check-out Date: %s
                Status: %s
                """,
                booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus()
        );
    }

    public static String buildBookingUpdatedMessage(
            Booking booking, UpdateBookingRequestDto requestDto) {
        return String.format(
                """
                🔄 Booking Updated:
                ID: %d
                Old Dates: %s to %s
                New Dates: %s to %s
                Status: %s
                """,
                booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate(),
                booking.getStatus()
        );
    }

    public static String buildBookingCancelledMessage(Booking booking) {
        return String.format(
                """
                ❌ Booking Cancelled:
                ID: %d
                Status: %s
                """,
                booking.getId(),
                booking.getStatus()
        );
    }

    public static String buildBookingCompletedMessage(Booking booking) {
        return String.format(
                """
                        ✅ Booking Completed:
                        ID: %d
                        Check-out Date: %s
                        Status: %s
                        """,
                booking.getId(),
                booking.getCheckOutDate(),
                booking.getStatus()
        );
    }

    public static String buildBookingExpiredMessage(Booking booking) {
        return String.format(
                """
                ❌ Booking Expired:
                ID: %d
                Check-in Date: %s
                Check-out Date: %s
                Status: %s
                """,
                booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus()
        );
    }

    /* Account */
    public static String buildBookingCompletedForAccountMessage(Booking booking) {
        return String.format("{\"userId\": \"%d\", \"increment\": 1}",
                booking.getUserId());
    }

    /* Payment */
    public static String buildBookingRefundMessage(Booking booking, double refundPercentage) {
        return String.format("{\"bookingId\": \"%d\", \"refundPercentage\": \"%f\"}",
                booking.getId(), refundPercentage);
    }
}
