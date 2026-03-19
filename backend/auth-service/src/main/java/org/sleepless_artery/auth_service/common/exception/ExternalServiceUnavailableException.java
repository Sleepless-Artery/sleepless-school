package org.sleepless_artery.auth_service.common.exception;

/**
 * Thrown when an external service is unavailable or fails to respond.
 */
public class ExternalServiceUnavailableException extends RuntimeException {
    public ExternalServiceUnavailableException(String message) {
        super(message);
    }
}