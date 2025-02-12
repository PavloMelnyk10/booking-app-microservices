package pavlo.melnyk.bookingservice.exception;

public class AccommodationFullyBookedException extends RuntimeException {
    public AccommodationFullyBookedException(String message) {
        super(message);
    }
}
