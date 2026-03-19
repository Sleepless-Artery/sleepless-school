package org.sleepless_artery.user_service.service.core;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.user_service.dto.UserRequestDto;
import org.sleepless_artery.user_service.dto.UserResponseDto;
import org.sleepless_artery.user_service.exception.*;
import org.sleepless_artery.user_service.logging.annotation.BusinessEvent;
import org.sleepless_artery.user_service.logging.event.LogEvent;
import org.sleepless_artery.user_service.mapper.UserMapper;
import org.sleepless_artery.user_service.repository.UserRepository;
import org.sleepless_artery.user_service.service.email.EmailChangeService;
import org.sleepless_artery.user_service.service.infrastructure.kafka.event.UserEventPublisher;
import org.sleepless_artery.user_service.service.util.TransactionUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * User management service.
 * <p>
 * Provides CRUD operations for users,
 * handles caching and coordinates email change workflow.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_ID_CACHE = "user:id";
    private static final String USER_EMAIL_CACHE = "user:email";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailChangeService emailChangeService;

    private final CacheManager cacheManager;
    private final UserEventPublisher eventPublisher;


    /**
     * Checks whether a user exists by identifier.
     *
     * @param id user identifier
     * @return {@code true} if user exists, {@code false} otherwise
     */
    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }


    /**
     * Returns user by identifier.
     *
     * @param id user identifier
     * @return user data
     * @throws UserNotFoundException if user does not exist
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = USER_ID_CACHE,
            key = "#id",
            condition = "#id != null"
    )
    public UserResponseDto getUserById(Long id) {
        return userMapper.toDto(
                userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id))
        );
    }


    /**
     * Returns user by email address.
     *
     * @param emailAddress user email
     * @return user data
     * @throws UserNotFoundException if email is not found
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = USER_EMAIL_CACHE,
            key = "#emailAddress",
            condition = "#emailAddress != null && !#emailAddress.isBlank()"
    )
    public UserResponseDto getUserByEmailAddress(String emailAddress) {
        return userMapper.toDto(
                userRepository.findByEmailAddress(emailAddress)
                        .orElseThrow(() -> new UserNotFoundException("Email address not found"))
        );
    }


    /**
     * Creates a new user.
     *
     * @param dto user creation data
     * @throws EmailAddressAlreadyExistsException if email already exists
     */
    @Override
    @BusinessEvent(LogEvent.USER_CREATION)
    @Transactional
    public void createUser(UserRequestDto dto) {
        if (userRepository.existsByEmailAddress(dto.getEmailAddress())) {
            throw new EmailAddressAlreadyExistsException("Email address already exists");
        }

        userRepository.save(userMapper.toEntity(dto));
    }


    /**
     * Updates user profile data.
     * <p>
     * Initiates email change process if email differs.
     *
     * @param id user identifier
     * @param dto updated user data
     * @return updated user data
     * @throws UserNotFoundException if user does not exist
     */
    @Override
    @BusinessEvent(LogEvent.USER_UPDATE)
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto dto) {

        var user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with ID: " + id));

        if (!user.getEmailAddress().equals(dto.getEmailAddress())) {
            emailChangeService.requestEmailChange(
                    user.getId(),
                    user.getEmailAddress(),
                    dto.getEmailAddress()
            );
        }

        user.setUsername(dto.getUsername());
        user.setInformation(dto.getInformation());

        var updatedUser = userRepository.save(user);

        TransactionUtils.runAfterCommit(() ->
                evictAndPublishUser(updatedUser.getId(), updatedUser.getEmailAddress())
        );

        return userMapper.toDto(updatedUser);
    }


    /**
     * Deletes user by identifier.
     * <p>
     * Invalidates caches and sends deletion event after transaction commit.
     *
     * @param id user identifier
     * @throws UserNotFoundException if user does not exist
     */
    @Override
    @BusinessEvent(LogEvent.USER_DELETION)
    @Transactional
    public void deleteUserById(Long id) {

        var user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with ID: " + id));

        userRepository.delete(user);

        TransactionUtils.runAfterCommit(() ->
                evictAndPublishUser(user.getId(), user.getEmailAddress())
        );
    }


    private void evictAndPublishUser(Long userId, String email) {
        var idCache = cacheManager.getCache(USER_ID_CACHE);
        var emailCache = cacheManager.getCache(USER_EMAIL_CACHE);

        if (idCache != null) {
            idCache.evict(userId);
        }

        if (emailCache != null) {
            emailCache.evict(email);
        }

        eventPublisher.publishUserDeletedEvent(userId, email);
    }
}