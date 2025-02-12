package pavlo.melnyk.bookingservice.exception;

public class AccommodationNotFoundException extends RuntimeException {
    public AccommodationNotFoundException(String message) {
        super(message);
    }
}
