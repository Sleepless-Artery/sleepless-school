package org.sleepless_artery.auth_service.credential.service.role_management;


/**
 * Service interface for managing roles assigned to user credentials.
 */
public interface CredentialRoleService {

    void addRoleToUser(String emailAddress, String roleName);

    void deleteRoleForUser(String emailAddress, String roleName);
}
