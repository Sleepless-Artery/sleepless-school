package org.sleepless_artery.submission_service.exception;

/**
 * Thrown when an assignment with the provided data does not exist.
 */
public class InvalidAssignmentIdException extends RuntimeException {
    public InvalidAssignmentIdException(String message) {
        super(message);
    }
}
