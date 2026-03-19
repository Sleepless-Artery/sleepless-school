package org.sleepless_artery.user_service.service.external.email;

/**
 * Service responsible for verifying email availability
 * in the authentication system.
 */
public interface EmailVerificationService {
    EmailAvailabilityVerificationResult verifyEmailAddressAvailability(String emailAddress);
}
