package pavlo.melnyk.paymentservice.service.payment;

import static pavlo.melnyk.paymentservice.service.notification.MessageBuilder.buildPaymentCanceledMessages;
import static pavlo.melnyk.paymentservice.service.notification.MessageBuilder.buildPaymentCreatedMessage;
import static pavlo.melnyk.paymentservice.service.notification.MessageBuilder.buildPaymentSuccessMessages;

import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pavlo.melnyk.paymentservice.client.AccountClient;
import pavlo.melnyk.paymentservice.client.BookingClient;
import pavlo.melnyk.paymentservice.dto.BookingDto;
import pavlo.melnyk.paymentservice.dto.BookingStatus;
import pavlo.melnyk.paymentservice.dto.PaymentDto;
import pavlo.melnyk.paymentservice.dto.UserDto;
import pavlo.melnyk.paymentservice.exception.AccessDeniedException;
import pavlo.melnyk.paymentservice.exception.EntityNotFoundException;
import pavlo.melnyk.paymentservice.mapper.PaymentMapper;
import pavlo.melnyk.paymentservice.model.Payment;
import pavlo.melnyk.paymentservice.model.PaymentStatus;
import pavlo.melnyk.paymentservice.repository.PaymentRepository;
import pavlo.melnyk.paymentservice.service.notification.KafkaNotificationProducer;
import pavlo.melnyk.paymentservice.service.notification.MessageBuilder;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final BookingClient bookingClient;
    private final PaymentValidationService paymentValidationService;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final AccountClient accountClient;
    private final KafkaNotificationProducer notificationProducer;

    @Override
    public PaymentDto createPaymentSession(final Long bookingId,
                                           final String successUrl,
                                           final String cancelUrl) {
        UserDto currentUser = accountClient.getCurrentUser();

        BookingDto booking = getBookingById(bookingId);

        paymentValidationService.validateBookingStatus(booking);
        paymentValidationService.validatePendingPaymentsForBooking(booking.getId());

        BigDecimal totalAmount = paymentValidationService.calculateTotalPrice(booking);
        Session session
                = stripeService.createStripeSession(booking, totalAmount, successUrl, cancelUrl);

        Payment payment = savePayment(booking.getId(), totalAmount, session, currentUser.getId());

        sendPaymentNotifications(bookingId, payment, totalAmount);

        return paymentMapper.toDto(payment);
    }

    @Override
    public String handleSuccess(final String sessionId) {
        Payment payment = paymentValidationService.getPaymentOrThrow(sessionId);

        Session session = stripeService.retrieveSession(sessionId);

        if ("paid".equals(session.getPaymentStatus())) {
            payment.setStatus(PaymentStatus.PAID);

            validateBookingAccess(payment);
            payment.setChargeId(stripeService.getChargeIdFromSession(session));
            paymentRepository.save(payment);

            sendPaymentSuccessNotifications(payment);

            return "Payment successfully completed for session: " + sessionId;
        }

        return "Payment not completed yet for session: " + sessionId
                + ". Current status: " + session.getPaymentStatus();
    }

    @Override
    public String handleCancel(final String sessionId) {
        Payment payment = paymentValidationService.getPaymentOrThrow(sessionId);
        stripeService.expireSession(sessionId);

        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);

        validateBookingAccess(payment);

        sendPaymentCancelNotifications(payment);

        return "Payment session with ID " + sessionId
                + " has been canceled, and booking marked as EXPIRED.";
    }

    @Override
    public void processRefund(Long bookingId, double refundPercentage) {
        Payment payment = paymentValidationService.getPaymentOrThrow(bookingId);

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("Refund is only allowed for paid payments");
        }

        BigDecimal refundAmount = payment.getAmount()
                .multiply(BigDecimal.valueOf(refundPercentage));

        stripeService.refundPayment(payment.getChargeId(), refundAmount);

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setAmountRefunded(refundAmount);
        paymentRepository.save(payment);
    }

    @Override
    public Page<PaymentDto> getCurrentUserPayments(final Pageable pageable) {
        UserDto currentUser = accountClient.getCurrentUser();
        return findAllByUserId(currentUser.getId(), pageable);
    }

    @Override
    public Page<PaymentDto> findAllByUserId(final Long userId, Pageable pageable) {
        return paymentRepository.findAllByUserId(userId, pageable)
                .map(paymentMapper::toDto);
    }

    @Override
    public Page<PaymentDto> getAllPayments(final Pageable pageable) {
        return paymentRepository.findAll(pageable).map(paymentMapper::toDto);
    }

    private Payment savePayment(final long bookingId,
                                final BigDecimal totalAmount,
                                final Session session,
                                final Long userId) {
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(totalAmount);
        payment.setSessionUrl(session.getUrl());
        payment.setSessionId(session.getId());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUserId(userId);
        return paymentRepository.save(payment);
    }

    private void validateBookingAccess(Payment payment) {
        BookingDto booking;
        try {
            booking = bookingClient.getBookingById(payment.getBookingId());
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException(
                    "You do not have access to booking with id: " + payment.getBookingId());
        }

        if (booking.getStatus() == BookingStatus.PENDING) {
            bookingClient.changeStatus(booking.getId(), BookingStatus.CONFIRMED);
        }
    }

    private BookingDto getBookingById(final Long bookingId) {
        try {
            return bookingClient.getBookingById(bookingId);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(
                    "Booking not found for the provided id: " + bookingId);
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException(
                    "You do not have access to booking with id: " + bookingId);
        }
    }

    private void sendPaymentNotifications(Long bookingId, Payment payment, BigDecimal totalAmount) {
        notificationProducer.sendNotification(
                "payment_created_topic", buildPaymentCreatedMessage(bookingId));
        notificationProducer.sendNotification("payment_topic",
                buildPaymentCreatedMessage(payment, totalAmount));
    }

    private void sendPaymentSuccessNotifications(Payment payment) {
        MessageBuilder.PaymentMessagesDto messages = buildPaymentSuccessMessages(payment);
        notificationProducer.sendNotification("payment_topic", messages.paymentMessage());
        notificationProducer.sendNotification("booking_topic", messages.bookingMessage());
    }

    private void sendPaymentCancelNotifications(Payment payment) {
        MessageBuilder.PaymentMessagesDto messages = buildPaymentCanceledMessages(payment);
        notificationProducer.sendNotification("payment_topic", messages.paymentMessage());
        notificationProducer.sendNotification("booking_topic", messages.bookingMessage());
    }
}
