package org.sleepless_artery.submission_service.exception;

/**
 * Thrown when a file management operation fails.
 */
public class FileOperationException extends RuntimeException {
    public FileOperationException(String message) {
        super(message);
    }
}
