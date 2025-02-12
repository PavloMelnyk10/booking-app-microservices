package pavlo.melnyk.paymentservice.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new EntityNotFoundException(
                    "Entity not found for the provided id");
        } else if (response.status() == 403) {
            return new AccessDeniedException("You do not have access to this resource");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
