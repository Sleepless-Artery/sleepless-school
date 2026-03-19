package org.sleepless_artery.enrollment_service.exception;

/**
 * Thrown when the provided student
 * has already been enrolled to the provided course.
 */
public class EnrollmentAlreadyExistsException extends RuntimeException {
    public EnrollmentAlreadyExistsException(String message) {
        super(message);
    }
}
