package org.sleepless_artery.submission_service.exception;

/**
 * Thrown when the file type is unsupported.
 */
public class UnsupportedFileTypeException extends RuntimeException {
    public UnsupportedFileTypeException(String message) {
        super(message);
    }
}
