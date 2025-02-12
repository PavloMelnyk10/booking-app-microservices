package pavlo.melnyk.paymentservice.service.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pavlo.melnyk.paymentservice.dto.PaymentDto;

public interface PaymentService {
    PaymentDto createPaymentSession(Long bookingId, String successUrl, String cancelUrl);

    String handleSuccess(String sessionId);

    String handleCancel(String sessionId);

    Page<PaymentDto> getCurrentUserPayments(Pageable pageable);

    Page<PaymentDto> findAllByUserId(Long userId, Pageable pageable);

    Page<PaymentDto> getAllPayments(Pageable pageable);

    void processRefund(Long bookingId, double refundPercentage);
}
