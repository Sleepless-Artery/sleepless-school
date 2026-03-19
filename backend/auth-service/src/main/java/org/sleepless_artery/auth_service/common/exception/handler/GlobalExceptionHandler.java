package org.sleepless_artery.auth_service.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.sleepless_artery.auth_service.common.exception.*;
import org.sleepless_artery.auth_service.common.exception.dto.ApiError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


/**
 * Global REST exception handler.
 *
 * <p>Intercepts application exceptions and converts them
 * into standardized HTTP responses.</p>
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialException.class)
    public ResponseEntity<ApiError> handleBadCredentialException(
            BadCredentialException ex,
            HttpServletRequest request
    ) {
        log.warn("Bad credential: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(CredentialAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleCredentialAlreadyExistsException(
            CredentialAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        log.warn("Credential already exists: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(CredentialNotFoundException.class)
    public ResponseEntity<ApiError> handleCredentialNotFoundException(
            CredentialNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Credential not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiError> handleRoleNotFoundException(
            RoleNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Role not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiError> handleTokenExpiredException(
            TokenExpiredException ex,
            HttpServletRequest request
    ) {
        log.warn("Token expired");
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Token expired", request);
    }

    @ExceptionHandler(TokenParsingException.class)
    public ResponseEntity<ApiError> handleTokenParsingException(
            TokenParsingException ex,
            HttpServletRequest request
    ) {
        log.error("Token parsing error", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Token parsing error", request);
    }

    @ExceptionHandler(ConfirmationException.class)
    public ResponseEntity<ApiError> handleConfirmationException(
            ConfirmationException ex,
            HttpServletRequest request
    ) {
        log.warn("Confirmation error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ApiError> handleKafkaException(
            KafkaException ex,
            HttpServletRequest request
    ) {
        log.error("Kafka error occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Kafka error occurred", request);
    }

    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleExternalServiceUnavailableException(
            ExternalServiceUnavailableException ex,
            HttpServletRequest request
    ) {
        log.warn("External service unavailable: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "External service temporarily unavailable",
                request
        );
    }

    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<ApiError> handleVerificationException(
            VerificationException ex,
            HttpServletRequest request
    ) {
        log.warn("Verification error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Verification error", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        log.warn("Access denied");
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Access denied", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        log.warn("Validation error");

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        log.error("Data integrity violation", ex);

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Data integrity violation",
                request
        );
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ApiError> handleInternalAuthenticationServiceException(
            InternalAuthenticationServiceException ex,
            HttpServletRequest request
    ) {
        log.warn("Authentication service error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication failed", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error", ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                request
        );
    }

    private ResponseEntity<ApiError> buildErrorResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
    }
}