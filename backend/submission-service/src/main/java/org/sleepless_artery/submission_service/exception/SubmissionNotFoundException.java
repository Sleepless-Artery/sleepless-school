package org.sleepless_artery.submission_service.exception;

/**
 * Thrown when a submission with the provided data does not exist.
 */
public class SubmissionNotFoundException extends RuntimeException {
    public SubmissionNotFoundException(String message) {
        super(message);
    }
}
