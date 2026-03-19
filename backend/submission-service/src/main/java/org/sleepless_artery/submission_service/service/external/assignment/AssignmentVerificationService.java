package org.sleepless_artery.submission_service.service.external.assignment;

/**
 * Service responsible for verifying assignment existence.
 */
public interface AssignmentVerificationService {
    AssignmentExistenceVerificationResult verifyAssignmentExistence(Long assignmentId);
}
