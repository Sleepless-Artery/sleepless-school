package org.sleepless_artery.auth_service.email.service.change;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.common.logging.annotation.BusinessEvent;
import org.sleepless_artery.auth_service.common.logging.event.LogEvent;
import org.sleepless_artery.auth_service.common.redis.RedisKeys;
import org.sleepless_artery.auth_service.config.kafka.KafkaTopicConfig;
import org.sleepless_artery.auth_service.credential.service.core.CredentialService;
import org.sleepless_artery.auth_service.authentication.dto.LoginDto;
import org.sleepless_artery.auth_service.common.exception.BadCredentialException;
import org.sleepless_artery.auth_service.common.exception.CredentialAlreadyExistsException;
import org.sleepless_artery.auth_service.credential.service.query.CredentialQueryService;
import org.sleepless_artery.auth_service.email.service.event.EmailEventPublisher;
import org.sleepless_artery.auth_service.email.service.reservation.EmailReservationService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;


/**
 * Service handling the email change workflow, including reservation and confirmation.
 */
@Service
@RequiredArgsConstructor
public class EmailChangeServiceImpl implements EmailChangeService {

    private final CredentialService credentialService;
    private final CredentialQueryService queryService;
    private final EmailEventPublisher emailEventPublisher;
    private final EmailReservationService emailReservationService;

    private final PasswordEncoder passwordEncoder;

    private final RedisTemplate<String, Object> redisTemplate;


    /**
     * Initiates the email change process by reserving the new email and sending confirmation.
     *
     * @param loginDto credentials to verify current password
     * @param newEmailAddress the new email address to switch to
     * @throws BadCredentialException if the current password is incorrect
     */
    @Override
    @Transactional
    @BusinessEvent(LogEvent.EMAIL_CHANGE)
    public void changeEmailAddress(LoginDto loginDto, String newEmailAddress) {
        var credential = queryService.findCredentialByEmailAddress(loginDto.getEmailAddress());

        if (loginDto.getPassword() == null ||
                !passwordEncoder.matches(loginDto.getPassword(), credential.getPasswordHash())
        ) {
            throw new BadCredentialException("Passwords do not match");
        }

        emailReservationService.reserveEmailAddress(
                newEmailAddress,
                loginDto,
                Duration.ofMinutes(60)
        );
        emailEventPublisher.publishEmailConfirmation(newEmailAddress, KafkaTopicConfig.CHANGE_EMAIL_TOPIC_NAME);
    }


    /**
     * Confirms the email change using a verification code.
     *
     * @param oldEmailAddress the current email address
     * @param newEmailAddress the new email address
     * @param confirmationCode the code sent to the new email
     */
    @Override
    @Transactional
    @BusinessEvent(LogEvent.EMAIL_CONFIRMATION)
    public void confirmEmailAddress(String oldEmailAddress, String newEmailAddress, String confirmationCode) {
        emailReservationService.checkReservation(newEmailAddress, confirmationCode);

        try {
            credentialService.changeEmailAddress(oldEmailAddress, newEmailAddress);
        } catch (DataIntegrityViolationException e) {
            throw new CredentialAlreadyExistsException("Email already exists");
        } finally {
            redisTemplate.delete(RedisKeys.EMAIL_RESERVATION_PREFIX + newEmailAddress);
            redisTemplate.delete(RedisKeys.EMAIL_RESERVATION_PREFIX + oldEmailAddress);
        }

        emailEventPublisher.publishEmailChanged(oldEmailAddress, newEmailAddress);
    }
}