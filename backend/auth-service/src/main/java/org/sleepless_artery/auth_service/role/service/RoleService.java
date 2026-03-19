package org.sleepless_artery.auth_service.role.service;

import org.sleepless_artery.auth_service.role.model.Role;

/**
 * Service interface for managing user roles.
 */
public interface RoleService {
    Role findRoleByName(String roleName);
}
