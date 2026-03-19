package org.sleepless_artery.auth_service.registration.service;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.authentication.service.AuthService;
import org.sleepless_artery.auth_service.common.logging.annotation.BusinessEvent;
import org.sleepless_artery.auth_service.common.logging.event.LogEvent;
import org.sleepless_artery.auth_service.common.redis.RedisKeys;
import org.sleepless_artery.auth_service.credential.service.core.CredentialService;
import org.sleepless_artery.auth_service.authentication.dto.LoginDto;
import org.sleepless_artery.auth_service.authentication.dto.JwtResponse;
import org.sleepless_artery.auth_service.email.service.event.EmailEventPublisher;
import org.sleepless_artery.auth_service.email.service.reservation.EmailReservationService;
import org.sleepless_artery.auth_service.registration.dto.RegistrationDto;
import org.sleepless_artery.auth_service.common.exception.BadCredentialException;
import org.sleepless_artery.auth_service.common.exception.CredentialAlreadyExistsException;
import org.sleepless_artery.auth_service.credential.model.Credential;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


/**
 * Service handling the user registration workflow.
 */
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final AuthService authService;
    private final CredentialService credentialService;
    private final EmailEventPublisher confirmationEventPublisher;
    private final EmailReservationService emailReservationService;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CHECK_EMAIL_TOPIC_POSTFIX = "check-email";


    /**
     * Starts the registration process by reserving the email and sending confirmation.
     *
     * @param credentialDto the registration data
     * @throws BadCredentialException if passwords do not match
     */
    @Override
    @BusinessEvent(LogEvent.REGISTRATION_INITIALIZATION)
    public void startRegistration(RegistrationDto credentialDto) {
        emailReservationService.reserveEmailAddress(
                credentialDto.getEmailAddress(),
                credentialDto,
                Duration.ofMinutes(60)
        );

        if (!credentialDto.getPassword().equals(credentialDto.getConfirmationPassword())) {
            redisTemplate.delete(RedisKeys.EMAIL_RESERVATION_PREFIX + credentialDto.getEmailAddress());
            throw new BadCredentialException("Passwords do not match");
        }

        confirmationEventPublisher.publishEmailConfirmation(
                credentialDto.getEmailAddress(),
                CHECK_EMAIL_TOPIC_POSTFIX
        );
    }


    /**
     * Confirms registration, creates the credential, and returns a JWT token.
     *
     * @param credentialDto the registration data
     * @param confirmationCode the code received via email
     * @return JwtResponse containing the access token
     * @throws CredentialAlreadyExistsException if email is taken during race condition
     */
    @Override
    @BusinessEvent(LogEvent.REGISTRATION_CONFIRMATION)
    public JwtResponse confirmRegistration(RegistrationDto credentialDto, String confirmationCode) {
        emailReservationService.checkReservation(
                credentialDto.getEmailAddress(),
                confirmationCode
        );

        Credential credential;
        try {
            credential = credentialService.createCredential(credentialDto);
        } catch (DataIntegrityViolationException e) {
            throw new CredentialAlreadyExistsException("Email already registered");
        } finally {
            redisTemplate.delete(RedisKeys.EMAIL_RESERVATION_PREFIX + credentialDto.getEmailAddress());
        }

        return authService.createAuthenticationToken(new LoginDto(
                credential.getEmailAddress(),
                credentialDto.getPassword()
        ));
    }
}