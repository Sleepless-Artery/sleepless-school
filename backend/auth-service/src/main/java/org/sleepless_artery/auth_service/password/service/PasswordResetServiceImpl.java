package org.sleepless_artery.auth_service.password.service;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.common.logging.annotation.BusinessEvent;
import org.sleepless_artery.auth_service.common.logging.event.LogEvent;
import org.sleepless_artery.auth_service.config.kafka.KafkaTopicConfig;
import org.sleepless_artery.auth_service.credential.service.core.CredentialService;
import org.sleepless_artery.auth_service.credential.service.query.CredentialQueryService;
import org.sleepless_artery.auth_service.email.service.event.EmailEventPublisher;
import org.sleepless_artery.auth_service.password.dto.PasswordResetDto;
import org.sleepless_artery.auth_service.common.exception.BadCredentialException;
import org.sleepless_artery.auth_service.common.exception.CredentialNotFoundException;
import org.sleepless_artery.auth_service.email.service.verification.VerificationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service handling the password reset workflow.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final CredentialService credentialService;
    private final CredentialQueryService queryService;
    private final EmailEventPublisher confirmationEventPublisher;
    private final VerificationService verificationService;

    private final PasswordEncoder passwordEncoder;


    /**
     * Initiates the password reset process by sending a confirmation email.
     *
     * @param emailAddress the email of the user requesting reset
     * @throws CredentialNotFoundException if the email does not exist
     */
    @Override
    @Transactional
    @BusinessEvent(LogEvent.PASSWORD_RESET_INITIALIZATION)
    public void initiatePasswordReset(String emailAddress) {
        if (!queryService.existsByEmailAddress(emailAddress)) {
            throw new CredentialNotFoundException();
        }
        confirmationEventPublisher.publishEmailConfirmation(emailAddress, KafkaTopicConfig.RESET_PASSWORD_TOPIC_NAME);
    }


    /**
     * Validates the reset code sent to the user.
     *
     * @param emailAddress the user's email
     * @param resetCode the code to validate
     * @return true if valid, false otherwise
     * @throws CredentialNotFoundException if credential with the provided email address does not exist
     */
    @Override
    @Transactional
    @BusinessEvent(LogEvent.PASSWORD_RESET_CODE_VERIFICATION)
    public boolean validatedResetCode(String emailAddress, String resetCode) {
        if (!queryService.existsByEmailAddress(emailAddress)) {
            throw new CredentialNotFoundException();
        }
        return verificationService.verifyAndDeleteCode(emailAddress, resetCode);
    }


    /**
     * Completes the password reset by updating the credential.
     *
     * @param passwordResetDto the DTO containing new password and confirmation
     * @throws BadCredentialException if passwords do not match
     */
    @Override
    @Transactional
    @BusinessEvent(LogEvent.PASSWORD_RESET_COMPLETION)
    public void completePasswordReset(PasswordResetDto passwordResetDto) {
        if (!passwordResetDto.getPassword().equals(passwordResetDto.getConfirmationPassword())) {
            throw new BadCredentialException("Passwords do not match");
        }

        var credential = queryService.findCredentialByEmailAddress(passwordResetDto.getEmailAddress());
        if (passwordEncoder.matches(passwordResetDto.getPassword(), credential.getPasswordHash())) {
            return;
        }

        credential.setPasswordHash(passwordEncoder.encode(passwordResetDto.getPassword()));
        credentialService.save(credential);
    }
}