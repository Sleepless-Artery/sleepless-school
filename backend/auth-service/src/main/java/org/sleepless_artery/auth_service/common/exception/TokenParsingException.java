package org.sleepless_artery.auth_service.common.exception;

/**
 * Thrown when a token cannot be parsed or validated.
 */
public class TokenParsingException extends RuntimeException {
    public TokenParsingException(String message) {
        super(message);
    }
}