package org.sleepless_artery.auth_service.email.service.verification;


/**
 * Service interface for generating and verifying temporary verification codes.
 */
public interface VerificationService {

    String saveVerificationCode(String emailAddress);

    boolean verifyAndDeleteCode(String emailAddress, String code);
}
