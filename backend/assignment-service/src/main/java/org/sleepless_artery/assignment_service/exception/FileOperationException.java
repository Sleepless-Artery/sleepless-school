package org.sleepless_artery.assignment_service.exception;

/**
 * Thrown when a file management operation fails.
 */
public class FileOperationException extends RuntimeException {
    public FileOperationException(String message) {
        super(message);
    }
}
