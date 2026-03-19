package org.sleepless_artery.submission_service.service.infrastructure.grpc.client.user;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.sleepless_artery.submission_service.UserVerificationServiceGrpc;
import org.sleepless_artery.submission_service.VerifyUserExistenceRequest;
import org.sleepless_artery.submission_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.submission_service.service.external.user.UserExistenceVerificationResult;
import org.sleepless_artery.submission_service.service.external.user.UserVerificationService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * gRPC client for user existence verification.
 * <p>
 * Communicates with external user service and applies resiliency mechanisms.
 */
@Service
@RequiredArgsConstructor
public class UserVerificationServiceGrpcClient implements UserVerificationService {

    @GrpcClient(USER_SERVICE)
    private UserVerificationServiceGrpc.UserVerificationServiceFutureStub futureStub;

    private static final String USER_SERVICE = "user-service";


    /**
     * Verifies user existence via user service.
     *
     * @param userId user identifier to verify
     * @return verification result
     * @throws ExternalServiceUnavailableException if service call fails
     */
    @Override
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "fallbackUserExistence")
    @Retry(name = USER_SERVICE)
    public UserExistenceVerificationResult verifyUserExistence(Long userId) {
        var request = VerifyUserExistenceRequest.newBuilder()
                .setUserId(userId)
                .build();

        try {
            var response = futureStub.withDeadlineAfter(1, TimeUnit.SECONDS)
                    .verifyUserExistence(request)
                    .get(1500, TimeUnit.MILLISECONDS);

            return response.getExistence()
                    ? UserExistenceVerificationResult.EXISTS
                    : UserExistenceVerificationResult.NOT_FOUND;

        } catch (Exception e) {
            throw new ExternalServiceUnavailableException("User service is unavailable");
        }
    }


    public UserExistenceVerificationResult fallbackUserExistence(Long userId, Throwable throwable) {
        return UserExistenceVerificationResult.SERVICE_UNAVAILABLE;
    }
}
