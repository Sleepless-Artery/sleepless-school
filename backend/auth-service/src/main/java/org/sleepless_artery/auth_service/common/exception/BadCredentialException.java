package org.sleepless_artery.auth_service.common.exception;

/**
 * Thrown when provided authentication credentials are invalid.
 */
public class BadCredentialException extends RuntimeException {
    public BadCredentialException(String message) {
        super(message);
    }
}