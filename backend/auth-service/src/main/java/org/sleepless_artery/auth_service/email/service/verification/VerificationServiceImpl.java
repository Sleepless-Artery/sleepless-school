package org.sleepless_artery.auth_service.email.service.verification;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.common.exception.VerificationException;
import org.sleepless_artery.auth_service.common.logging.annotation.BusinessEvent;
import org.sleepless_artery.auth_service.common.logging.event.LogEvent;
import org.sleepless_artery.auth_service.common.redis.RedisKeys;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;


/**
 * Service for generating and verifying temporary codes (stored in Redis).
 */
@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final RedisTemplate<String, String> redisTemplate;


    /**
     * Generates and saves a 6-digit verification code for an email.
     *
     * @param emailAddress the target email address
     * @return the generated verification code
     * @throws VerificationException if Redis operation fails
     */
    @Override
    public String saveVerificationCode(String emailAddress) {
        var verificationCode = generateConfirmationCode();
        var key = RedisKeys.VERIFICATION_CODE_PREFIX + emailAddress;

        redisTemplate.delete(key);

        try {
            redisTemplate.opsForValue().set(
                    key,
                    verificationCode,
                    Duration.ofMinutes(60)
            );
        } catch (Exception e) {
            throw new VerificationException("Failed to save verification code");
        }

        return verificationCode;
    }


    /**
     * Verifies a code and deletes it upon success.
     *
     * @param emailAddress the email address
     * @param code the code to verify
     * @return {@code true} if valid, {@code false} otherwise
     */
    @Override
    @BusinessEvent(LogEvent.CONFIRMATION_CODE_VERIFICATION)
    public boolean verifyAndDeleteCode(String emailAddress, String code) {
        var key = RedisKeys.VERIFICATION_CODE_PREFIX + emailAddress;

        var storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }


    private String generateConfirmationCode() {
        var random = new SecureRandom();
        var bytes = new byte[3];
        random.nextBytes(bytes);

        return String.format(
                "%06d",
                ((bytes[0] & 0xFF) << 16 |
                        (bytes[1] & 0xFF) << 8 |
                        (bytes[2] & 0xFF)) % 1000000
        );
    }
}