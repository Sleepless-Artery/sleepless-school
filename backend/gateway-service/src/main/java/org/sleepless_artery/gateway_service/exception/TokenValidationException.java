package org.sleepless_artery.gateway_service.exception;

/**
 * Thrown when JWT token validation fails.
 */
public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String message) {
        super(message);
    }
}
