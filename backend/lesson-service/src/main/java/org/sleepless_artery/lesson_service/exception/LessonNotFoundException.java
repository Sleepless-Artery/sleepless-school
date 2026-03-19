package org.sleepless_artery.lesson_service.exception;

/**
 * Thrown when a lesson with the provided data does not exist.
 */
public class LessonNotFoundException extends RuntimeException {
    public LessonNotFoundException(String message) {
        super(message);
    }
}
