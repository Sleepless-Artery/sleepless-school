package org.sleepless_artery.course_service.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Holds authenticated user information extracted from gateway headers.
 */
public final class RequestUserContext {

    private final Long userId;
    private final String email;
    private final Set<String> roles;

    public RequestUserContext(Long userId, String email, Set<String> roles) {
        this.userId = userId;
        this.email = email;
        this.roles = roles;
    }

    public static RequestUserContext from(HttpServletRequest request) {
        var idHeader = request.getHeader("X-User-Id");
        var email = request.getHeader("X-User-Email");
        var rolesHeader = request.getHeader("X-User-Roles");

        if (idHeader == null || idHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authenticated user identifier header");
        }

        Long userId;
        try {
            userId = Long.valueOf(idHeader);
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authenticated user identifier header");
        }

        Set<String> roles = rolesHeader == null || rolesHeader.isBlank()
                ? Set.of()
                : Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .collect(Collectors.toSet());

        return new RequestUserContext(userId, email, roles);
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
