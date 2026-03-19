package org.sleepless_artery.assignment_service.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.sleepless_artery.assignment_service.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;


/**
 * Global REST exception handler.
 *
 * <p>Intercepts application exceptions and converts them
 * into standardized HTTP responses.</p>
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            AssignmentNotFoundException.class,
            InvalidLessonIdException.class
    })
    public ResponseEntity<Object> handleNotFound(RuntimeException ex) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }


    @ExceptionHandler(AssignmentAlreadyExistsException.class)
    public ResponseEntity<Object> handleConflict(RuntimeException ex) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                null
        );
    }


    @ExceptionHandler({
            UnsupportedFileTypeException.class,
            IllegalDeadlineException.class
    })
    public ResponseEntity<Object> handleBadRequest(RuntimeException ex) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                null
        );
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            FieldError fieldError = (FieldError) error;
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("field", fieldError.getField());
            errorMap.put("message", fieldError.getDefaultMessage());
            errors.add(errorMap);
        });

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                errors
        );
    }


    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ResponseEntity<Object> handleExternalServiceUnavailable(ExternalServiceUnavailableException ex) {
        log.warn("External service unavailable: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage(),
                null
        );
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation", ex);

        String message = "Data integrity violation. Check unique constraints.";

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                message,
                ex.getRootCause() != null
                        ? ex.getRootCause().getMessage()
                        : null
        );
    }

    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<Object> handleKafkaException(KafkaException ex) {
        log.error("Kafka error", ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Kafka error: " + ex.getMessage(),
                null
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.",
                null
        );
    }


    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message, Object details) {

        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        if (details != null) {
            body.put("details", details);
        }

        return new ResponseEntity<>(body, status);
    }
}
