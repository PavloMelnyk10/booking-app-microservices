package pavlo.melnyk.paymentservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Security filter that authenticates requests based on headers set by API
 * Gateway.
 */
public class InternalAuthFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_EMAIL = "X-User-Email";
    private static final String HEADER_USER_NAME = "X-User-Name";
    private static final String HEADER_USER_FIRST_NAME = "X-User-First-Name";
    private static final String HEADER_USER_LAST_NAME = "X-User-Last-Name";
    private static final String HEADER_USER_ROLES = "X-User-Roles";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String userId = request.getHeader(HEADER_USER_ID);

        if (userId != null && !userId.isEmpty()) {
            String email = getHeaderOrEmpty(request, HEADER_USER_EMAIL);
            String username = getHeaderOrEmpty(request, HEADER_USER_NAME);
            String firstName = getHeaderOrEmpty(request, HEADER_USER_FIRST_NAME);
            String lastName = getHeaderOrEmpty(request, HEADER_USER_LAST_NAME);
            String roles = getHeaderOrEmpty(request, HEADER_USER_ROLES);

            InternalUserPrincipal principal = new InternalUserPrincipal(
                    userId, email, username, firstName, lastName, roles);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getHeaderOrEmpty(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        return value != null ? value : "";
    }
}
