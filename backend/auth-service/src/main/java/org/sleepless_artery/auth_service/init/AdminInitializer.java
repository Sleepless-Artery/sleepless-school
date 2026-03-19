package org.sleepless_artery.auth_service.init;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.common.logging.annotation.Loggable;
import org.sleepless_artery.auth_service.credential.model.Credential;
import org.sleepless_artery.auth_service.credential.service.core.CredentialService;
import org.sleepless_artery.auth_service.credential.service.query.CredentialQueryService;
import org.sleepless_artery.auth_service.role.service.RoleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


/**
 * Application runner that initializes the default ADMIN user on startup.
 */
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final CredentialService credentialService;
    private final CredentialQueryService queryService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_ROLE = "ADMIN";

    @Value("${admin.email-address}")
    private String adminEmailAddress;

    @Value("${admin.password}")
    private String adminPassword;


    /**
     * Executes the admin initialization logic.
     *
     * @param args command line arguments
     */
    @Override
    @Transactional
    @Loggable
    public void run(ApplicationArguments args) {
        if (queryService.existsByEmailAddress(adminEmailAddress)) {
            return;
        }

        var adminRole = roleService.findRoleByName(ADMIN_ROLE);

        var adminCredential = Credential.builder()
                .emailAddress(adminEmailAddress)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .roles(Set.of(adminRole))
                .build();

        credentialService.save(adminCredential);
    }
}