package org.sleepless_artery.user_service.service.email;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.user_service.dto.UserResponseDto;
import org.sleepless_artery.user_service.exception.*;
import org.sleepless_artery.user_service.logging.annotation.BusinessEvent;
import org.sleepless_artery.user_service.logging.event.LogEvent;
import org.sleepless_artery.user_service.mapper.UserMapper;
import org.sleepless_artery.user_service.repository.UserRepository;
import org.sleepless_artery.user_service.service.infrastructure.grpc.client.EmailVerificationServiceGrpcClient;
import org.sleepless_artery.user_service.service.util.TransactionUtils;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;


/**
 * User email change service.
 * <p>
 * Manages two-step email change workflow:
 * reservation and confirmation.
 */
@Service
@RequiredArgsConstructor
public class EmailChangeServiceImpl implements EmailChangeService {

    private static final String USER_ID_CACHE = "user:id";
    private static final String USER_EMAIL_CACHE = "user:email";

    private static final String EMAIL_CHANGE_LOCK_PREFIX = "email_change_request:";
    private static final Duration EMAIL_CHANGE_LOCK_TTL = Duration.ofMinutes(60);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailVerificationServiceGrpcClient grpcClient;
    private final CacheManager cacheManager;

    /**
     * Initiates email change process.
     * <p>
     * Reserves new email address for the user.
     *
     * @param userId user identifier
     * @param oldEmail current email
     * @param newEmail requested new email
     * @throws EmailAddressAlreadyExistsException if email is occupied or reserved
     * @throws ExternalServiceUnavailableException if verification service is unavailable
     */
    @Override
    @BusinessEvent(LogEvent.EMAIL_RESERVATION)
    @Transactional
    public void requestEmailChange(Long userId, String oldEmail, String newEmail) {
        if (oldEmail.equals(newEmail)) {
            return;
        }
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("Invalid email address");
        }

        verifyEmailAvailability(newEmail);
        reserveEmail(newEmail, userId);
    }

    /**
     * Confirms email change and updates user email.
     *
     * @param token email confirmation token
     * @param newEmail confirmed new email
     * @return updated user data
     * @throws EmailAddressAlreadyExistsException if email already exists
     * @throws UserNotFoundException if user does not exist
     */
    @Override
    @BusinessEvent(LogEvent.EMAIL_CONFIRMATION)
    @Transactional
    public UserResponseDto confirmEmailAddressChange(String token, String newEmail) {
        if (userRepository.findByEmailAddress(newEmail).isPresent()) {
            throw new EmailAddressAlreadyExistsException("Email already exists");
        }

        var lockKey = EMAIL_CHANGE_LOCK_PREFIX + newEmail;
        var userId = redisTemplate.opsForValue().get(lockKey);

        if (userId == null) {
            throw new IllegalArgumentException("Email confirmation expired or invalid");
        }

        var user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        var oldEmail = user.getEmailAddress();
        user.setEmailAddress(newEmail);
        var updatedUser = userRepository.save(user);

        redisTemplate.delete(lockKey);

        TransactionUtils.runAfterCommit(() -> {
            var idCache = cacheManager.getCache(USER_ID_CACHE);
            var emailCache = cacheManager.getCache(USER_EMAIL_CACHE);

            if (idCache != null) {
                idCache.put(user.getId(), userMapper.toDto(updatedUser));
            }

            if (emailCache != null) {
                emailCache.evict(oldEmail);
                emailCache.put(updatedUser.getEmailAddress(), userMapper.toDto(updatedUser));
            }
        });

        return userMapper.toDto(updatedUser);
    }

    private void verifyEmailAvailability(String email) {
        switch (grpcClient.verifyEmailAddressAvailability(email)) {
            case OCCUPIED ->
                    throw new EmailAddressAlreadyExistsException("Email already occupied");
            case SERVICE_UNAVAILABLE ->
                    throw new ExternalServiceUnavailableException("Auth service unavailable");
        }
    }

    private void reserveEmail(String email, Long userId) {
        var reserved = redisTemplate.opsForValue().setIfAbsent(
                EMAIL_CHANGE_LOCK_PREFIX + email,
                userId.toString(),
                EMAIL_CHANGE_LOCK_TTL
        );

        if (Boolean.FALSE.equals(reserved)) {
            throw new EmailAddressAlreadyExistsException("Email awaiting confirmation");
        }
    }
}