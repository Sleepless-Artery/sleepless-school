package org.sleepless_artery.assignment_service.exception;

/**
 * Thrown when a lesson with the provided data does not exist.
 */
public class InvalidLessonIdException extends RuntimeException {
    public InvalidLessonIdException(String message) {
        super(message);
    }
}
