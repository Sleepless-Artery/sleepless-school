package org.sleepless_artery.course_service.service.external.user;

/**
 * Service responsible for verifying user existence.
 */
public interface UserExistenceChecker {
    UserExistenceVerificationResult verifyUserExistence(Long userId);
}
