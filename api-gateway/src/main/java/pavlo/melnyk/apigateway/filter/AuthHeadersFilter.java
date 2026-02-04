package pavlo.melnyk.apigateway.filter;

import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter that extracts authenticated user information from JWT
 * and forwards it to downstream services via HTTP headers.
 * 
 * Headers forwarded:
 * - X-User-Id: Keycloak user ID (sub claim)
 * - X-User-Email: User's email
 * - X-User-Name: User's preferred username
 * - X-User-First-Name: User's first name
 * - X-User-Last-Name: User's last name
 * - X-User-Roles: Comma-separated list of roles
 */
@Component
public class AuthHeadersFilter implements GlobalFilter, Ordered {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_EMAIL = "X-User-Email";
    private static final String HEADER_USER_NAME = "X-User-Name";
    private static final String HEADER_USER_FIRST_NAME = "X-User-First-Name";
    private static final String HEADER_USER_LAST_NAME = "X-User-Last-Name";
    private static final String HEADER_USER_ROLES = "X-User-Roles";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .map(jwt -> addAuthHeaders(exchange, jwt))
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    private ServerWebExchange addAuthHeaders(ServerWebExchange exchange, Jwt jwt) {
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(HEADER_USER_ID, getClaimOrEmpty(jwt, "sub"))
                .header(HEADER_USER_EMAIL, getClaimOrEmpty(jwt, "email"))
                .header(HEADER_USER_NAME, getClaimOrEmpty(jwt, "preferred_username"))
                .header(HEADER_USER_FIRST_NAME, getClaimOrEmpty(jwt, "given_name"))
                .header(HEADER_USER_LAST_NAME, getClaimOrEmpty(jwt, "family_name"))
                .header(HEADER_USER_ROLES, extractRoles(jwt))
                .build();

        return exchange.mutate().request(mutatedRequest).build();
    }

    private String getClaimOrEmpty(Jwt jwt, String claim) {
        Object value = jwt.getClaim(claim);
        return value != null ? value.toString() : "";
    }

    private String extractRoles(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("spring_sec_roles");
        if (roles == null || roles.isEmpty()) {
            return "";
        }
        return String.join(",", roles);
    }

    @Override
    public int getOrder() {
        // Run after authentication but before routing
        return Ordered.LOWEST_PRECEDENCE - 100;
    }
}
