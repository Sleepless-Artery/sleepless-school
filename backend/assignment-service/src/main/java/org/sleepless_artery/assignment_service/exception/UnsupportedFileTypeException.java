package org.sleepless_artery.assignment_service.exception;

/**
 * Thrown when the file type is unsupported.
 */
public class UnsupportedFileTypeException extends RuntimeException {
    public UnsupportedFileTypeException(String message) {
        super(message);
    }
}
