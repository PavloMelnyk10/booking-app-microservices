package pavlo.melnyk.paymentservice.service.notification;

import java.math.BigDecimal;
import pavlo.melnyk.paymentservice.dto.BookingStatus;
import pavlo.melnyk.paymentservice.model.Payment;

public final class MessageBuilder {
    private MessageBuilder() {
    }

    public record PaymentMessagesDto(String paymentMessage, String bookingMessage) {}

    /* Booking */
    public static String buildPaymentCreatedMessage(Long bookingId) {
        return String.format("{\"bookingId\": \"%d\"}",
                bookingId);
    }

    /* Payment */
    public static String buildPaymentCreatedMessage(Payment payment, BigDecimal totalAmount) {
        return String.format(
                """
                💳 Payment Created:
                %s
                Booking ID: %d
                Amount Due: $%.2f
                
                """,
                buildPaymentBookingInfo(payment),
                payment.getBookingId(),
                totalAmount
        );
    }

    private static String buildPaymentBookingInfo(Payment payment) {
        return String.format(
                """
                Payment ID: %d
                Booking ID: %d
                Payment Status: %s
                """,
                payment.getId(),
                payment.getBookingId(),
                payment.getStatus()
        );
    }

    public static PaymentMessagesDto buildPaymentSuccessMessages(Payment payment) {
        String paymentMsg = String.format(
                """
                ✅ Payment Successful:
                %s
                Booking ID: %d
                Amount Paid: $%.2f
                """,
                buildPaymentBookingInfo(payment),
                payment.getBookingId(),
                payment.getAmount()
        );

        String bookingMsg = String.format(
                """
                ✅ Booking confirmed and paid:
                ID: %d
                Status: %s
                """,
                payment.getBookingId(),
                BookingStatus.CONFIRMED
        );

        return new PaymentMessagesDto(paymentMsg, bookingMsg);
    }

    public static PaymentMessagesDto buildPaymentCanceledMessages(Payment payment) {
        String paymentMsg = String.format(
                """
                ❌ Payment Cancelled:
                %s
                """,
                buildPaymentBookingInfo(payment)
        );

        String bookingMsg = String.format(
                """
                ❌ Booking Cancelled:
                ID: %d
                Status: %s
                """,
                payment.getBookingId(),
                BookingStatus.CANCELLED
        );

        return new PaymentMessagesDto(paymentMsg, bookingMsg);
    }

    public static String buildPaymentExpiredMessage(Payment payment) {
        return String.format(
                """
                ❌ Payment Expired:
                Booking ID: %d
                Payment Status: %s
                """,
                payment.getBookingId(),
                payment.getStatus()
        );
    }

    public static String buildBookingExpiredDueToPaymentMessage(Long bookingId) {
        return String.format(
                """
                ❌ Booking Expired due to Payment:
                ID: %d
                Status: %s
                """,
                bookingId,
                BookingStatus.CANCELLED
        );
    }
}
