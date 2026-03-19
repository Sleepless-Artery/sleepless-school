package org.sleepless_artery.course_service.exception;

/**
 * Thrown when the provided author does not exist.
 */
public class AuthorDoesNotExistException extends RuntimeException {
    public AuthorDoesNotExistException(String message) {
        super(message);
    }
}
