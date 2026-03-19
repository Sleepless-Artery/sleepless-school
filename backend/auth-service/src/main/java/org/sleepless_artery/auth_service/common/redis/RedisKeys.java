package org.sleepless_artery.auth_service.common.redis;

import lombok.NoArgsConstructor;

/**
 * Utility class containing constant prefixes for Redis keys.
 */
@NoArgsConstructor
public final class RedisKeys {

    public static final String EMAIL_RESERVATION_PREFIX = "email_reservation:";
    public static final String VERIFICATION_CODE_PREFIX = "verification_code:";
}