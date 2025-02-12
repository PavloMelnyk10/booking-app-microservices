package pavlo.melnyk.bookingservice.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import pavlo.melnyk.bookingservice.exception.CustomFeignErrorDecoder;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getCredentials() instanceof Jwt jwt) {
                System.out.println(jwt.getClaimAsString("given_name"));
                requestTemplate.header("Authorization",
                        "Bearer " + jwt.getTokenValue());
            }
        };
    }

    @Bean
    public ErrorDecoder customFeignErrorDecoder() {
        return new CustomFeignErrorDecoder();
    }
}
