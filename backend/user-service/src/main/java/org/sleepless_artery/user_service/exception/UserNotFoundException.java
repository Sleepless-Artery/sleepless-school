package org.sleepless_artery.user_service.exception;

/**
 * Thrown when a user with the provided data does not exist.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
