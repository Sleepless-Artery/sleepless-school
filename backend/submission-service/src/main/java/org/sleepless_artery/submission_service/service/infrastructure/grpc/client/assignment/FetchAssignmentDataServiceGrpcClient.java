package org.sleepless_artery.submission_service.service.infrastructure.grpc.client.assignment;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.sleepless_artery.submission_service.FetchAssignmentDataServiceGrpc;
import org.sleepless_artery.submission_service.GetCorrectOptionsIndicesRequest;
import org.sleepless_artery.submission_service.GetMaxScoreRequest;
import org.sleepless_artery.submission_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.submission_service.service.external.assignment.FetchAssignmentDataService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * gRPC client implementation of {@link FetchAssignmentDataService}.
 * <p>
 * Retrieves assignment-related data from the remote assignment service and applies resiliency mechanisms.
 * </p>
 *
 */
@Service
@RequiredArgsConstructor
public class FetchAssignmentDataServiceGrpcClient implements FetchAssignmentDataService {

    @GrpcClient(ASSIGNMENT_SERVICE)
    private FetchAssignmentDataServiceGrpc.FetchAssignmentDataServiceFutureStub futureStub;

    private static final String ASSIGNMENT_SERVICE = "assignment-service";


    /**
     * Retrieves the indices of correct answer options for a test assignment.
     *
     * @param assignmentId identifier of the assignment
     * @return list of indices representing correct answer options
     *
     * @throws ExternalServiceUnavailableException if the remote service
     * cannot be reached or fails to respond within the deadline
     */
    @Override
    @CircuitBreaker(name = ASSIGNMENT_SERVICE, fallbackMethod = "fallbackCorrectOptions")
    @Retry(name = ASSIGNMENT_SERVICE)
    public List<Integer> getCorrectOptionsIndices(Long assignmentId) {
        var request = GetCorrectOptionsIndicesRequest.newBuilder()
                .setAssignmentId(assignmentId)
                .build();

        try {
            return futureStub
                    .withDeadlineAfter(1, TimeUnit.SECONDS)
                    .getCorrectOptionsIndices(request)
                    .get(1500, TimeUnit.MILLISECONDS)
                    .getCorrectOptionsIndicesList();

        } catch (Exception e) {
            throw new ExternalServiceUnavailableException("Assignment service is unavailable");
        }
    }


    /**
     * Retrieves the maximum possible score for an assignment.
     *
     * @param assignmentId identifier of the assignment
     * @return maximum score defined for the assignment
     *
     * @throws ExternalServiceUnavailableException if the remote service
     * cannot be reached or fails to respond
     */
    @Override
    @CircuitBreaker(name = ASSIGNMENT_SERVICE, fallbackMethod = "fallbackMaxScore")
    @Retry(name = ASSIGNMENT_SERVICE)
    public Double getMaxScore(Long assignmentId) {
        var request = GetMaxScoreRequest.newBuilder()
                .setAssignmentId(assignmentId)
                .build();

        try {
            return futureStub
                    .withDeadlineAfter(1, TimeUnit.SECONDS)
                    .getMaxScore(request)
                    .get(1500, TimeUnit.MILLISECONDS)
                    .getMaxScore();

        } catch (Exception e) {
            throw new ExternalServiceUnavailableException("Assignment service is unavailable");
        }
    }


    public List<Integer> fallbackCorrectOptions(Long assignmentId, Throwable exception) {
        return List.of();
    }

    public Double fallbackMaxScore(Long assignmentId, Throwable ex) {
        return -1.;
    }
}