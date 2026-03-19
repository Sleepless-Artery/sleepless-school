package org.sleepless_artery.auth_service.common.exception;

/**
 * Thrown when attempting to create a credential that already exists in the system.
 */
public class CredentialAlreadyExistsException extends RuntimeException {
    public CredentialAlreadyExistsException(String message) {
        super(message);
    }
}