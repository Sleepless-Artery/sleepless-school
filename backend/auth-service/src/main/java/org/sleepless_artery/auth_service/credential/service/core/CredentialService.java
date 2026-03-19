package org.sleepless_artery.auth_service.credential.service.core;

import org.sleepless_artery.auth_service.registration.dto.RegistrationDto;
import org.sleepless_artery.auth_service.credential.model.Credential;


/**
 * Service interface for managing user credentials.
 */
public interface CredentialService {

    Credential createCredential(RegistrationDto registrationDto);

    void save(Credential credential);

    void changeEmailAddress(String oldEmailAddress, String newEmailAddress);

    void deleteByEmailAddress(String emailAddress);
}