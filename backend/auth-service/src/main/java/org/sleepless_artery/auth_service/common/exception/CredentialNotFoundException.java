package org.sleepless_artery.auth_service.common.exception;

/**
 * Exception thrown when a requested credential cannot be found.
 */
public class CredentialNotFoundException extends RuntimeException {
    public CredentialNotFoundException() {
        super();
    }
}