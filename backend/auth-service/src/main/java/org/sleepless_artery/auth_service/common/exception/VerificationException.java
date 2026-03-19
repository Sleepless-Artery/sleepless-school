package org.sleepless_artery.auth_service.common.exception;

/**
 * Thrown when a verification process fails.
 */
public class VerificationException extends RuntimeException {
    public VerificationException(String message) {
        super(message);
    }
}