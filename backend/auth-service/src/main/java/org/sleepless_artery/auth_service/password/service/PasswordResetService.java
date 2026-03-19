package org.sleepless_artery.auth_service.password.service;

import org.sleepless_artery.auth_service.password.dto.PasswordResetDto;


/**
 * Service interface for handling the password reset workflow.
 * Includes initiation, code validation, and password update steps.
 */
public interface PasswordResetService {

    void initiatePasswordReset(String emailAddress);

    boolean validatedResetCode(String emailAddress, String resetCode);

    void completePasswordReset(PasswordResetDto passwordResetDto);
}
