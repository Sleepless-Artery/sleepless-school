package org.sleepless_artery.auth_service.common.exception;

/**
 * Thrown when a confirmation process fails.
 */
public class ConfirmationException extends RuntimeException {
    public ConfirmationException(String message) {
        super(message);
    }
}