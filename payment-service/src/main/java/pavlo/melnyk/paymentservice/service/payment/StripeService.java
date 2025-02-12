package pavlo.melnyk.paymentservice.service.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.ChargeListParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import pavlo.melnyk.paymentservice.dto.BookingDto;
import pavlo.melnyk.paymentservice.exception.StripeServiceException;

@Service
public class StripeService {
    public Session retrieveSession(final String sessionId) {
        try {
            return Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new StripeServiceException("Error retrieving session from Stripe", e);
        }
    }

    public void expireSession(final String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            if ("open".equals(session.getStatus())) {
                session.expire();
            }
        } catch (StripeException e) {
            throw new StripeServiceException("Error expiring session in Stripe", e);
        }
    }

    public Session createStripeSession(final BookingDto booking,
                                       final BigDecimal totalAmount,
                                       final String successUrl,
                                       final String cancelUrl) {
        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(totalAmount.movePointRight(2).longValue())
                                .setProductData(SessionCreateParams.LineItem
                                        .PriceData.ProductData.builder()
                                        .setName("Booking #" + booking.getId())
                                        .build())
                                .build())
                        .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .build();

        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new StripeServiceException("Error creating Stripe session", e);
        }
    }

    public Refund refundPayment(String chargeId, BigDecimal refundAmount) {
        if (chargeId == null || chargeId.isEmpty()) {
            throw new IllegalArgumentException("Charge ID is required for a refund.");
        }

        Long amountInCents = refundAmount.movePointRight(2).longValue();

        RefundCreateParams params = RefundCreateParams.builder()
                .setCharge(chargeId)
                .setAmount(amountInCents)
                .build();
        try {
            return Refund.create(params);
        } catch (StripeException e) {
            throw new StripeServiceException("Error processing refund", e);
        }
    }

    public String getChargeIdFromSession(Session session) {
        try {
            String paymentIntentId = session.getPaymentIntent();

            if (paymentIntentId == null || paymentIntentId.isEmpty()) {
                throw new StripeServiceException("PaymentIntent not found in session");
            }

            ChargeListParams chargeListParams = ChargeListParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .build();

            List<Charge> charges = Charge.list(chargeListParams).getData();

            if (charges == null || charges.isEmpty()) {
                throw new StripeServiceException(
                        "No charge found for PaymentIntent " + paymentIntentId);
            }

            return charges.stream()
                    .filter(charge -> "succeeded".equals(charge.getStatus()))
                    .map(Charge::getId)
                    .findFirst()
                    .orElseThrow(() -> new StripeServiceException(
                            "No successful charge found for PaymentIntent " + paymentIntentId));

        } catch (StripeException e) {
            throw new StripeServiceException("Error retrieving chargeId from Stripe", e);
        }
    }

}
