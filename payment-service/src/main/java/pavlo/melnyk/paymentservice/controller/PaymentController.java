package pavlo.melnyk.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import pavlo.melnyk.paymentservice.dto.PaymentDto;
import pavlo.melnyk.paymentservice.service.payment.PaymentService;

@Tag(name = "Payment Management", description = "Endpoints for managing payments")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Create a payment session",
            description = "Create a new payment session for a specific booking."
                    + " Returns session details.")
    public PaymentDto createPayment(
            @RequestParam("bookingId") Long bookingId,
            UriComponentsBuilder uriBuilder) {

        UriComponentsBuilder uriComponents = (UriComponentsBuilder) uriBuilder.clone();
        String successUrl = uriBuilder
                .path("/payments/success")
                .queryParam("sessionId", "{CHECKOUT_SESSION_ID}")
                .build()
                .toUriString();

        String cancelUrl = uriComponents
                .path("/payments/cancel")
                .queryParam("sessionId", "{CHECKOUT_SESSION_ID}")
                .build()
                .toUriString();

        return paymentService.createPaymentSession(bookingId, successUrl, cancelUrl);
    }

    @GetMapping("/success")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Handle successful payment",
            description = "Processes a successful payment using the Stripe session ID.")
    public String success(@RequestParam("sessionId") String sessionId) {
        return paymentService.handleSuccess(sessionId);
    }

    @GetMapping("/cancel")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Handle canceled payment",
            description = "Handles a canceled payment session and updates"
                    + " the payment and booking statuses.")
    public String cancel(@RequestParam("sessionId") String sessionId) {
        return paymentService.handleCancel(sessionId);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get current user's payments",
            description = "Retrieve a paginated list of payments for "
                    + "the currently authenticated user.")
    public ResponseEntity<Page<PaymentDto>> getCurrentUserPayments(
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(paymentService.getCurrentUserPayments(pageable));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get payments by user ID",
            description = "Retrieve a paginated list of payments for a specific user. Admins only.")
    public ResponseEntity<Page<PaymentDto>> getPaymentsByUserId(
            @RequestParam("userId") Long userId, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(paymentService.findAllByUserId(userId, pageable));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all payments",
            description = "Retrieve a paginated list of all payments. Admins only.")
    public ResponseEntity<Page<PaymentDto>> getAllPayments(Pageable pageable) {
        return ResponseEntity.ok(paymentService.getAllPayments(pageable));
    }
}
