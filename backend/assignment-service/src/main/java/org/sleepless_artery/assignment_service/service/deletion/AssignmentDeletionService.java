package org.sleepless_artery.assignment_service.service.deletion;

/**
 * Service for bulk assignment deletion.
 */
public interface AssignmentDeletionService {
    void deleteAssignmentsByLessonId(Long lessonId);
}
