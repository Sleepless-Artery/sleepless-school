package org.sleepless_artery.auth_service.email.service.reservation;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.common.exception.ConfirmationException;
import org.sleepless_artery.auth_service.common.exception.CredentialAlreadyExistsException;
import org.sleepless_artery.auth_service.common.logging.annotation.BusinessEvent;
import org.sleepless_artery.auth_service.common.logging.event.LogEvent;
import org.sleepless_artery.auth_service.common.redis.RedisKeys;
import org.sleepless_artery.auth_service.credential.service.query.CredentialQueryService;
import org.sleepless_artery.auth_service.email.service.verification.VerificationService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


/**
 * Service for managing temporary email reservations in Redis.
 */
@Service
@RequiredArgsConstructor
public class EmailReservationServiceImpl implements EmailReservationService {

    private final CredentialQueryService queryService;
    private final VerificationService verificationService;
    private final RedisTemplate<String, Object> redisTemplate;


    /**
     * Checks if an email address is available for registration or change.
     *
     * @param emailAddress the email to check
     * @return {@code true} if available, {@code false} if reserved or exists in DB
     */
    @Override
    public boolean isEmailAddressAvailable(String emailAddress) {
        var key = RedisKeys.EMAIL_RESERVATION_PREFIX + emailAddress;

        return emailAddress != null
                && !redisTemplate.hasKey(key)
                && !queryService.existsByEmailAddress(emailAddress);
    }


    /**
     * Reserves an email address in Redis for a specific duration.
     *
     * @param emailAddress the email to reserve
     * @param reservationData the data associated with the reservation (e.g., DTO)
     * @param reservationDuration the TTL for the reservation
     * @throws CredentialAlreadyExistsException if the email is already reserved
     */
    @Override
    @BusinessEvent(LogEvent.EMAIL_RESERVATION)
    public void reserveEmailAddress(String emailAddress, Object reservationData, Duration reservationDuration) {
        var key = RedisKeys.EMAIL_RESERVATION_PREFIX + emailAddress;

        var reserved = redisTemplate.opsForValue().setIfAbsent(
                key,
                reservationData,
                reservationDuration
        );

        if (Boolean.FALSE.equals(reserved)) {
            throw new CredentialAlreadyExistsException("Email address is already reserved");
        }

        if (queryService.existsByEmailAddress(emailAddress)) {
            redisTemplate.delete(key);
            throw new CredentialAlreadyExistsException("Email address already exists");
        }
    }


    /**
     * Validates a reservation and verification code.
     *
     * @param emailAddress the email address
     * @param confirmationCode the code to verify
     * @throws ConfirmationException if reservation missing or code invalid
     */
    @Override
    public void checkReservation(String emailAddress, String confirmationCode) {
        var key = RedisKeys.EMAIL_RESERVATION_PREFIX + emailAddress;

        if (!redisTemplate.hasKey(key)) {
            throw new ConfirmationException("Email was not reserved for change");
        }

        if (!verificationService.verifyAndDeleteCode(emailAddress, confirmationCode)) {
            throw new ConfirmationException("Wrong confirmation code");
        }
    }
}
