package pavlo.melnyk.accommodationservice.exception;

public class AccommodationNotFoundException extends RuntimeException {
    public AccommodationNotFoundException(String message) {
        super(message);
    }
}
