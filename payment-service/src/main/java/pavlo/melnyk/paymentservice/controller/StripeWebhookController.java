package pavlo.melnyk.paymentservice.controller;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pavlo.melnyk.paymentservice.service.payment.PaymentService;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {
        try {
            Event event = Webhook.constructEvent(payload,
                    signature, "YOUR_STRIPE_SECRET");

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject().orElseThrow();
                paymentService.handleSuccess(session.getId());
            }
            return ResponseEntity.ok("Received");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Webhook error: " + e.getMessage());
        }
    }
}
