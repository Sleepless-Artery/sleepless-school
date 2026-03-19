package org.sleepless_artery.course_service.exception;

/**
 * Thrown when a course with the provided data does not exist.
 */
public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(String message) {
        super(message);
    }
}
