package org.sleepless_artery.auth_service.registration.service;

import org.sleepless_artery.auth_service.authentication.dto.JwtResponse;
import org.sleepless_artery.auth_service.registration.dto.RegistrationDto;


/**
 * Service interface for handling the user registration workflow.
 * Includes email reservation and confirmation.
 */
public interface RegistrationService {

    void startRegistration(RegistrationDto registrationDto);

    JwtResponse confirmRegistration(RegistrationDto registrationDto, String confirmationCode);
}
