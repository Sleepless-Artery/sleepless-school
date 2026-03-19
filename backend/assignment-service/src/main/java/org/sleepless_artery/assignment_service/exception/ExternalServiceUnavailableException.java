package org.sleepless_artery.assignment_service.exception;

/**
 * Thrown when an external service is unavailable.
 */
public class ExternalServiceUnavailableException extends RuntimeException {
    public ExternalServiceUnavailableException(String message) {
        super(message);
    }
}
