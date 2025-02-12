package pavlo.melnyk.bookingservice.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();

        Map<String, Object> body = createErrorBody(
                status.value(),
                "Validation Failed",
                errors
        );

        return new ResponseEntity<>(body, headers, status);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError) {
            String fieldName = ((FieldError) e).getField();
            String message = e.getDefaultMessage();
            return fieldName + ": " + message;
        }
        return e.getDefaultMessage();
    }

    @ExceptionHandler(AccommodationNotFoundException.class)
    public ResponseEntity<Object> handleAccommodationNotFound(
            AccommodationNotFoundException ex, WebRequest request) {

        Map<String, Object> body = createErrorBody(
                HttpStatus.NOT_FOUND.value(),
                "Accommodation not found",
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    private Map<String, Object> createErrorBody(int status, String error, List<String> messages) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("messages", messages);
        return body;
    }

    @ExceptionHandler(AccommodationFullyBookedException.class)
    public ResponseEntity<Object> handleFullyBookedException(AccommodationFullyBookedException ex) {
        Map<String, Object> body = createErrorBody(
                HttpStatus.CONFLICT.value(),
                "Fully booked",
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
        Map<String, Object> body = createErrorBody(
                HttpStatus.BAD_REQUEST.value(),
                "error",
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<Object> handleBookingNotFound(
            BookingNotFoundException ex, WebRequest request) {

        Map<String, Object> body = createErrorBody(
                HttpStatus.NOT_FOUND.value(),
                "Booking not found",
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {

        Map<String, Object> body = createErrorBody(
                HttpStatus.FORBIDDEN.value(),
                "Access denied",
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
}
