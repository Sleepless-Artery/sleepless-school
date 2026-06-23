package org.sleepless_artery.user_service.service.core;

import org.sleepless_artery.user_service.dto.UserRequestDto;
import org.sleepless_artery.user_service.dto.UserResponseDto;

import java.util.Set;


/**
 * User management service.
 * <p>
 * Provides operations for user creation, retrieval,
 * update and deletion.
 */
public interface UserService {

    boolean existsById(Long id);

    UserResponseDto getUserById(Long id);

    UserResponseDto getUserByEmailAddress(String emailAddress);

    void createUser(UserRequestDto user);

    UserResponseDto updateUser(Long id, UserRequestDto user);

    UserResponseDto updateUser(Long id, UserRequestDto user, Long currentUserId, Set<String> currentUserRoles);

    void deleteUserById(Long id);

    void deleteUserById(Long id, Long currentUserId, Set<String> currentUserRoles);
}