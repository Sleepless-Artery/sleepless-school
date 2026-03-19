package org.sleepless_artery.notification_service.exception;

/**
 * Thrown when sending a message fails.
 */
public class SendingEmailException extends RuntimeException {
    public SendingEmailException(String message) {
        super(message);
    }
}
