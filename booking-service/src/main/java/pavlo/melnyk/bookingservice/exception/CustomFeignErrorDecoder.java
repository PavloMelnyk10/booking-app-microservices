package pavlo.melnyk.bookingservice.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new AccommodationNotFoundException(
                    "Accommodation not found for the provided id");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
