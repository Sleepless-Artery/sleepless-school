package org.sleepless_artery.user_service.service.email;

import org.sleepless_artery.user_service.dto.UserResponseDto;


/**
 * Service responsible for handling user email address changes.
 * <p>
 * Coordinates email change requests and confirmation
 * after receiving an event from the authentication service.
 */
public interface EmailChangeService {

    void requestEmailChange(Long userId, String oldEmailAddress, String newEmailAddress);

    UserResponseDto confirmEmailAddressChange(String oldEmailAddress, String newEmailAddress);
}
