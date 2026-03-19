package org.sleepless_artery.auth_service.credential.service.core;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.common.logging.annotation.BusinessEvent;
import org.sleepless_artery.auth_service.common.logging.event.LogEvent;
import org.sleepless_artery.auth_service.config.cache.CacheConfig;
import org.sleepless_artery.auth_service.credential.service.query.CredentialQueryService;
import org.sleepless_artery.auth_service.integration.client.user.UserCreationService;
import org.sleepless_artery.auth_service.registration.dto.RegistrationDto;
import org.sleepless_artery.auth_service.common.exception.CredentialAlreadyExistsException;
import org.sleepless_artery.auth_service.common.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.auth_service.credential.model.Credential;
import org.sleepless_artery.auth_service.credential.repository.CredentialRepository;
import org.sleepless_artery.auth_service.role.service.RoleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


/**
 * Service implementation for managing user credentials (create, update, delete).
 * Handles transactional operations and cache eviction.
 */
@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {

    private final CredentialRepository credentialRepository;
    private final CredentialQueryService queryService;
    private final RoleService roleService;
    private final UserCreationService userCreationService;
    private final PasswordEncoder passwordEncoder;

    private static final String USER_ROLE = "USER";


    /**
     * Creates a new user credential with encoded password and default role.
     *
     * @param registrationDto the registration data containing email and password
     * @return the saved Credential entity
     * @throws CredentialAlreadyExistsException if the email is already registered
     * @throws ExternalServiceUnavailableException if the external user service is down
     */
    @Override
    @Transactional
    @BusinessEvent(LogEvent.CREATION_CREDENTIAL)
    public Credential createCredential(RegistrationDto registrationDto) {
        if (credentialRepository.existsByEmailAddress(registrationDto.getEmailAddress())) {
            throw new CredentialAlreadyExistsException("Credential already exists");
        }

        switch (userCreationService.createUser(registrationDto.getEmailAddress())) {
            case FAILURE -> throw new CredentialAlreadyExistsException("Email address is already occupied");
            case SERVICE_UNAVAILABLE -> throw new ExternalServiceUnavailableException("User service unavailable");
        }

        var role = roleService.findRoleByName(USER_ROLE);

        var credential = Credential.builder()
                .emailAddress(registrationDto.getEmailAddress())
                .passwordHash(passwordEncoder.encode(registrationDto.getPassword()))
                .roles(Set.of(role))
                .build();

        return credentialRepository.save(credential);
    }


    /**
     * Saves a credential entity and evicts related cache entries.
     *
     * @param credential the credential entity to save
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.CREDENTIAL_CACHE_NAME, key = "#credential.emailAddress")
    })
    public void save(Credential credential) {
        credentialRepository.save(credential);
    }


    /**
     * Changes the email address of an existing credential.
     *
     * @param oldEmailAddress the current email address
     * @param newEmailAddress the new email address to set
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.CREDENTIAL_CACHE_NAME, key = "#oldEmailAddress")
    })
    public void changeEmailAddress(String oldEmailAddress, String newEmailAddress) {
        var credential = queryService.findCredentialByEmailAddress(oldEmailAddress);
        credential.setEmailAddress(newEmailAddress);
        credentialRepository.save(credential);
    }


    /**
     * Deletes a credential by email address.
     *
     * @param emailAddress the email address of the credential to delete
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.CREDENTIAL_CACHE_NAME, key = "#emailAddress")
    })
    @BusinessEvent(LogEvent.DELETION_CREDENTIAL)
    public void deleteByEmailAddress(String emailAddress) {
        credentialRepository.deleteByEmailAddress(emailAddress);
    }
}