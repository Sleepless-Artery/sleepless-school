package org.sleepless_artery.auth_service.integration.client.user;

/**
 * Service interface for creating users in external services
 */
public interface UserCreationService {
    UserCreationResult createUser(String emailAddress);
}
