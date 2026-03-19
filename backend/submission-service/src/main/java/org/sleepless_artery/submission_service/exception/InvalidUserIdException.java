package org.sleepless_artery.submission_service.exception;

/**
 * Thrown when a user with the provided data does not exist.
 */
public class InvalidUserIdException extends RuntimeException {
    public InvalidUserIdException(String message) {
        super(message);
    }
}
