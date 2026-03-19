package org.sleepless_artery.submission_service.service.validation.existence;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.submission_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.submission_service.exception.InvalidAssignmentIdException;
import org.sleepless_artery.submission_service.exception.InvalidUserIdException;
import org.sleepless_artery.submission_service.logging.annotations.BusinessEvent;
import org.sleepless_artery.submission_service.logging.event.LogEvent;
import org.sleepless_artery.submission_service.service.external.assignment.AssignmentVerificationService;
import org.sleepless_artery.submission_service.service.external.user.UserVerificationService;
import org.springframework.stereotype.Service;


/**
 * gRPC-based implementation of {@link CommonExistenceValidator}.
 * <p>
 * Validates the existence of both assignments and users.
 */
@Service
@RequiredArgsConstructor
public class GrpcExistenceValidator implements CommonExistenceValidator {

    private final AssignmentVerificationService assignmentVerificationService;
    private final UserVerificationService userVerificationService;


    /**
     * Validates that both assignment and student exist.
     *
     * @param assignmentId identifier of the assignment
     * @param studentId identifier of the student
     *
     * @throws InvalidAssignmentIdException if the assignment does not exist
     * @throws InvalidUserIdException if the student does not exist
     * @throws ExternalServiceUnavailableException if any remote service is unavailable
     */
    @Override
    @BusinessEvent(LogEvent.EXISTENCE_VALIDATION)
    public void validateExistence(Long assignmentId, Long studentId) {

        switch (assignmentVerificationService.verifyAssignmentExistence(assignmentId)) {
            case NOT_FOUND -> throw new InvalidAssignmentIdException("Assignment not found with ID: " + assignmentId);
            case SERVICE_UNAVAILABLE -> throw new ExternalServiceUnavailableException("Service unavailable");
        }

        switch (userVerificationService.verifyUserExistence(studentId)) {
            case NOT_FOUND -> throw new InvalidUserIdException("Student not found with ID: " + studentId);
            case SERVICE_UNAVAILABLE -> throw new ExternalServiceUnavailableException("Service unavailable");
        }
    }
}
