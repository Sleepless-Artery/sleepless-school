package org.sleepless_artery.enrollment_service.exception;

/**
 * Thrown when an external service is unavailable.
 */
public class ExternalServiceUnavailableException extends RuntimeException {
    public ExternalServiceUnavailableException(String message) {
        super(message);
    }
}
