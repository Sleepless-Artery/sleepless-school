package org.sleepless_artery.enrollment_service.exception;

/**
 * Thrown when the provided user does not exist.
 */
public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(String message) {
        super(message);
    }
}
