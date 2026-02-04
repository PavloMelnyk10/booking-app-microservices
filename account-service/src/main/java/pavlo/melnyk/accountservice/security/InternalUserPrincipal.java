package pavlo.melnyk.accountservice.security;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Represents an authenticated user from internal headers passed by API Gateway.
 */
public class InternalUserPrincipal {
    private final String userId;
    private final String email;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final List<GrantedAuthority> authorities;

    public InternalUserPrincipal(String userId, String email, String username,
            String firstName, String lastName, String rolesHeader) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authorities = parseRoles(rolesHeader);
    }

    private List<GrantedAuthority> parseRoles(String rolesHeader) {
        List<GrantedAuthority> result = new ArrayList<>();
        if (rolesHeader != null && !rolesHeader.isEmpty()) {
            for (String role : rolesHeader.split(",")) {
                String trimmedRole = role.trim();
                if (!trimmedRole.isEmpty()) {
                    result.add(new SimpleGrantedAuthority(trimmedRole));
                }
            }
        }
        return result;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public boolean hasRole(String role) {
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(role)
                        || a.getAuthority().equals("ROLE_" + role));
    }
}
