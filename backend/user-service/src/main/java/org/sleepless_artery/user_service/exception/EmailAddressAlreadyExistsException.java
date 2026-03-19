package org.sleepless_artery.user_service.exception;

/**
 * Thrown when an email address is already occupied or reserved.
 */
public class EmailAddressAlreadyExistsException extends RuntimeException {
    public EmailAddressAlreadyExistsException(String message) {
        super(message);
    }
}
