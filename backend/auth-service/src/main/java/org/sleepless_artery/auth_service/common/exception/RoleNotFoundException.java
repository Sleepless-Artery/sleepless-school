package org.sleepless_artery.auth_service.common.exception;

/**
 * Thrown when a requested role cannot be found.
 */
public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}