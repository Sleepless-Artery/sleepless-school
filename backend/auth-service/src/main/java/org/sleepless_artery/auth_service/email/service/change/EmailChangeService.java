package org.sleepless_artery.auth_service.email.service.change;

import org.sleepless_artery.auth_service.authentication.dto.LoginDto;


/**
 * Service interface for handling the email change workflow.
 * Includes reservation, confirmation, and final update steps.
 */
public interface EmailChangeService {

    void changeEmailAddress(LoginDto loginDto, String newEmailAddress);

    void confirmEmailAddress(String oldEmailAddress, String newEmailAddress, String confirmationCode);
}
