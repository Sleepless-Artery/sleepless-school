package org.sleepless_artery.submission_service.service.external.user;

/**
 * Service responsible for verifying user existence.
 */
public interface UserVerificationService {
    UserExistenceVerificationResult verifyUserExistence(Long userId);
}
