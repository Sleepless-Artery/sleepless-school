package org.sleepless_artery.assignment_service.exception;

/**
 * Thrown when an assignment with the provided data does not exist.
 */
public class AssignmentNotFoundException extends RuntimeException {
    public AssignmentNotFoundException(String message) {
        super(message);
    }
}
