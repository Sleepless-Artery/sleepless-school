package org.sleepless_artery.auth_service.credential.service.query;

import org.sleepless_artery.auth_service.credential.model.Credential;
import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * Read-only service interface for retrieving credentials and loading user details.
 * Extends Spring Security's UserDetailsService for authentication support.
 */
public interface CredentialQueryService extends UserDetailsService {

    Credential findCredentialByEmailAddress(String emailAddress);

    boolean existsByEmailAddress(String emailAddress);
}
