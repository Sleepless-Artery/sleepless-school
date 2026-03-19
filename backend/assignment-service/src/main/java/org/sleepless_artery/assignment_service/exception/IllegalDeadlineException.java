package org.sleepless_artery.assignment_service.exception;

/**
 * Thrown when an assignment deadline is incorrect.
 */
public class IllegalDeadlineException extends RuntimeException {
    public IllegalDeadlineException(String message) {
        super(message);
    }
}
