package org.sleepless_artery.enrollment_service.exception;

/**
 * Thrown when an enrollment with the provided data does not exist.
 */
public class EnrollmentNotFoundException extends RuntimeException {
    public EnrollmentNotFoundException(String message) {
        super(message);
    }
}
