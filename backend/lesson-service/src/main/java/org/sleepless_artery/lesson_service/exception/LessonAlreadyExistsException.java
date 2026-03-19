package org.sleepless_artery.lesson_service.exception;

/**
 * Thrown when a lesson with the provided data already exists.
 */
public class LessonAlreadyExistsException extends RuntimeException {
    public LessonAlreadyExistsException(String message) {
        super(message);
    }
}
