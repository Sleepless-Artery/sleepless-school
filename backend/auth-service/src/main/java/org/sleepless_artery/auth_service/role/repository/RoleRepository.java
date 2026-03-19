package org.sleepless_artery.auth_service.role.repository;

import org.sleepless_artery.auth_service.role.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);
}
