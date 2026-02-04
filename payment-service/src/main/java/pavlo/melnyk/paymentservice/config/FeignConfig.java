package pavlo.melnyk.paymentservice.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pavlo.melnyk.paymentservice.exception.CustomFeignErrorDecoder;

/**
 * Feign configuration for inter-service communication.
 * Forwards internal authentication headers from Gateway to downstream services.
 */
@Configuration
public class FeignConfig {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_EMAIL = "X-User-Email";
    private static final String HEADER_USER_NAME = "X-User-Name";
    private static final String HEADER_USER_FIRST_NAME = "X-User-First-Name";
    private static final String HEADER_USER_LAST_NAME = "X-User-Last-Name";
    private static final String HEADER_USER_ROLES = "X-User-Roles";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            if (attrs instanceof ServletRequestAttributes) {
                HttpServletRequest request = ((ServletRequestAttributes) attrs).getRequest();

                forwardHeader(requestTemplate, request, HEADER_USER_ID);
                forwardHeader(requestTemplate, request, HEADER_USER_EMAIL);
                forwardHeader(requestTemplate, request, HEADER_USER_NAME);
                forwardHeader(requestTemplate, request, HEADER_USER_FIRST_NAME);
                forwardHeader(requestTemplate, request, HEADER_USER_LAST_NAME);
                forwardHeader(requestTemplate, request, HEADER_USER_ROLES);
            }
        };
    }

    private void forwardHeader(feign.RequestTemplate template,
            HttpServletRequest request,
            String headerName) {
        String value = request.getHeader(headerName);
        if (value != null && !value.isEmpty()) {
            template.header(headerName, value);
        }
    }

    @Bean
    public ErrorDecoder customFeignErrorDecoder() {
        return new CustomFeignErrorDecoder();
    }
}
