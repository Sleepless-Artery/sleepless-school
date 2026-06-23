package org.sleepless_artery.course_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the current user is not authorized to perform an action.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenActionException extends RuntimeException {

    public ForbiddenActionException(String message) {
        super(message);
    }
}
