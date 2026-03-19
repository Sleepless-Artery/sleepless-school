package org.sleepless_artery.submission_service.service.infrastructure.grpc.client.assignment;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.sleepless_artery.submission_service.AssignmentVerificationServiceGrpc;
import org.sleepless_artery.submission_service.VerifyAssignmentExistenceRequest;
import org.sleepless_artery.submission_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.submission_service.service.external.assignment.AssignmentExistenceVerificationResult;
import org.sleepless_artery.submission_service.service.external.assignment.AssignmentVerificationService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * gRPC client for assignment existence verification.
 * <p>
 * Communicates with external assignment service and applies resiliency mechanisms.
 */
@Service
@RequiredArgsConstructor
public class AssignmentVerificationServiceGrpcClient implements AssignmentVerificationService {

    @GrpcClient(ASSIGNMENT_SERVICE)
    private AssignmentVerificationServiceGrpc.AssignmentVerificationServiceFutureStub futureStub;

    private static final String ASSIGNMENT_SERVICE = "assignment-service";


    /**
     * Verifies assignment existence via assignment service.
     *
     * @param assignmentId assignment identifier to verify
     * @return verification result
     * @throws ExternalServiceUnavailableException if service call fails
     */
    @Override
    @CircuitBreaker(name = ASSIGNMENT_SERVICE, fallbackMethod = "fallbackAssignmentExistence")
    @Retry(name = ASSIGNMENT_SERVICE)
    public AssignmentExistenceVerificationResult verifyAssignmentExistence(Long assignmentId) {

        var request = VerifyAssignmentExistenceRequest.newBuilder()
                .setAssignmentId(assignmentId)
                .build();

        try {
            var response = futureStub.withDeadlineAfter(30, TimeUnit.SECONDS)
                    .verifyAssignmentExistence(request)
                    .get();

            return response.getExistence()
                    ? AssignmentExistenceVerificationResult.EXISTS
                    : AssignmentExistenceVerificationResult.NOT_FOUND;

        } catch (Exception e) {
            throw new ExternalServiceUnavailableException("Assignment service is unavailable");
        }
    }


    private AssignmentExistenceVerificationResult fallbackAssignmentExistence(Long assignmentId, Throwable ex) {
        return AssignmentExistenceVerificationResult.SERVICE_UNAVAILABLE;
    }
}
