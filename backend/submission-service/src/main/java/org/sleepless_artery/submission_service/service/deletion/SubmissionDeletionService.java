package org.sleepless_artery.submission_service.service.deletion;


/**
 * Service for bulk submission deletion.
 */
public interface SubmissionDeletionService {

    void deleteAllByAssignmentId(Long assignmentId);

    void deleteAllByStudentId(Long studentId);
}
