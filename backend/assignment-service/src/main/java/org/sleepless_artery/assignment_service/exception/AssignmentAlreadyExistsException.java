package org.sleepless_artery.assignment_service.exception;

/**
 * Thrown when an assignment with the provided data already exists.
 */
public class AssignmentAlreadyExistsException extends RuntimeException {
    public AssignmentAlreadyExistsException(String message) {
        super(message);
    }
}
