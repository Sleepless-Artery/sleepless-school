package org.sleepless_artery.assignment_service.service.external.lesson;

/**
 * Service responsible for verifying lesson existence.
 */
public interface LessonExistenceChecker {
    LessonExistenceVerificationResult verifyLessonExistence(Long lessonId);
}
