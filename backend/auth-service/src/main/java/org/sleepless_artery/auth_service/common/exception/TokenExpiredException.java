package org.sleepless_artery.auth_service.common.exception;

/**
 * Thrown when a security token has expired.
 */
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}