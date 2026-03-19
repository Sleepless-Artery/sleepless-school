package org.sleepless_artery.lesson_service.exception;

/**
 * Thrown when the provided course does not exist.
 */
public class CourseDoesNotExistException extends RuntimeException {
    public CourseDoesNotExistException(String message) {
        super(message);
    }
}
