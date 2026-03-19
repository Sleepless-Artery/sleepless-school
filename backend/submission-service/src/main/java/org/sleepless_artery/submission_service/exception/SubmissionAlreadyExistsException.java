package org.sleepless_artery.submission_service.exception;

/**
 * Thrown when a submission with the provided data already exists.
 */
public class SubmissionAlreadyExistsException extends RuntimeException {
    public SubmissionAlreadyExistsException(String message) {
        super(message);
    }
}
