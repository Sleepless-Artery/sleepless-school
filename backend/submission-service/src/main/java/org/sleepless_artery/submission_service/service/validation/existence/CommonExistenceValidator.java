package org.sleepless_artery.submission_service.service.validation.existence;

/**
 * Contract for validating the existence of required domain entities:
 * assignment and student.
 */
public interface CommonExistenceValidator {
    void validateExistence(Long assignmentId, Long studentId);
}
