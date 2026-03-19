package org.sleepless_artery.enrollment_service.service.external.course;

/**
 * Service responsible for verifying course existence.
 */
public interface CourseExistenceChecker {
    CourseExistenceVerificationResult verifyCourseExistence(Long courseId);
}
