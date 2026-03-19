package org.sleepless_artery.auth_service.credential.service.role_management;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.common.logging.annotation.BusinessEvent;
import org.sleepless_artery.auth_service.common.logging.event.LogEvent;
import org.sleepless_artery.auth_service.credential.service.core.CredentialService;
import org.sleepless_artery.auth_service.credential.service.query.CredentialQueryService;
import org.sleepless_artery.auth_service.role.service.RoleService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service for managing roles assigned to user credentials.
 */
@Service
@RequiredArgsConstructor
public class CredentialRoleServiceImpl implements CredentialRoleService {

    private final CredentialService credentialService;
    private final CredentialQueryService queryService;
    private final RoleService roleService;


    /**
     * Adds a specific role to a user's credential.
     *
     * @param emailAddress the email of the user
     * @param roleName the name of the role to add
     */
    @Override
    @Transactional
    @BusinessEvent(LogEvent.ADDING_ROLE_FOR_USER)
    public void addRoleToUser(String emailAddress, String roleName) {
        var credential = queryService.findCredentialByEmailAddress(emailAddress);
        credential.getRoles().add(roleService.findRoleByName(roleName));
        credentialService.save(credential);
    }


    /**
     * Removes a specific role from a user's credential.
     *
     * @param emailAddress the email of the user
     * @param roleName the name of the role to remove
     */
    @Override
    @Transactional
    @BusinessEvent(LogEvent.DELETING_ROLE_FOR_USER)
    public void deleteRoleForUser(String emailAddress, String roleName) {
        var credential = queryService.findCredentialByEmailAddress(emailAddress);
        credential.getRoles().remove(roleService.findRoleByName(roleName));
        credentialService.save(credential);
    }
}