package pavlo.melnyk.paymentservice.exception;

public class StripeServiceException extends RuntimeException {
    public StripeServiceException(String message) {
        super(message);
    }

    public StripeServiceException(String message, Exception e) {
        super(message, e);
    }
}
