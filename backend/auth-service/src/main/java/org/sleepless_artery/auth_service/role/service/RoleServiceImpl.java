package org.sleepless_artery.auth_service.role.service;

import lombok.AllArgsConstructor;
import org.sleepless_artery.auth_service.common.exception.RoleNotFoundException;
import org.sleepless_artery.auth_service.config.cache.CacheConfig;
import org.sleepless_artery.auth_service.role.model.Role;
import org.sleepless_artery.auth_service.role.repository.RoleRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service for retrieving user roles.
 */
@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    /**
     * Finds a role by its name. Result is cached.
     *
     * @param roleName the name of the role (e.g., "ADMIN", "USER")
     * @return the Role entity
     * @throws RoleNotFoundException if the role does not exist
     */
    @Override
    @Cacheable(value = CacheConfig.ROLE_CACHE_NAME, key = "#roleName")
    @Transactional(readOnly = true)
    public Role findRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role '" + roleName + "' does not exist"));
    }
}