package org.sleepless_artery.course_service.exception;

/**
 * Thrown when a course with the provided data already exists.
 */
public class CourseAlreadyExistsException extends RuntimeException {
    public CourseAlreadyExistsException(String message) {
        super(message);
    }
}
