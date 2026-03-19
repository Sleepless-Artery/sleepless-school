package org.sleepless_artery.auth_service.credential.service.query;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.common.exception.CredentialNotFoundException;
import org.sleepless_artery.auth_service.config.cache.CacheConfig;
import org.sleepless_artery.auth_service.credential.model.Credential;
import org.sleepless_artery.auth_service.credential.repository.CredentialRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;


/**
 * Read-only service for retrieving credentials and loading user details for Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CredentialQueryServiceImpl implements CredentialQueryService {

    private final CredentialRepository credentialRepository;


    /**
     * Loads user details by email address for Spring Security authentication.
     *
     * @param emailAddress the email address of the user
     * @return UserDetails object containing authorities and password hash
     * @throws UsernameNotFoundException if no credential is found for the email
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailAddress) throws UsernameNotFoundException {

        var credential = credentialRepository.findByEmailAddress(emailAddress)
                .orElseThrow(CredentialNotFoundException::new);

        Set<SimpleGrantedAuthority> authorities =
                credential.getRoles() == null
                        ? Set.of()
                        : credential.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                        .collect(Collectors.toSet());

        return new User(
                credential.getEmailAddress(),
                credential.getPasswordHash(),
                authorities
        );
    }


    /**
     * Finds a credential entity by email address.
     *
     * @param emailAddress the email address to search for
     * @return the found Credential entity
     * @throws CredentialNotFoundException if no credential is found
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CREDENTIAL_CACHE_NAME, key = "#emailAddress")
    public Credential findCredentialByEmailAddress(String emailAddress) {
        return credentialRepository.findByEmailAddress(emailAddress)
                .orElseThrow(CredentialNotFoundException::new);
    }


    /**
     * Checks if a credential exists for the given email.
     *
     * @param emailAddress the email address to check
     * @return {@code true} if exists, {@code false} otherwise
     */
    @Override
    public boolean existsByEmailAddress(String emailAddress) {
        return credentialRepository.existsByEmailAddress(emailAddress);
    }
}
