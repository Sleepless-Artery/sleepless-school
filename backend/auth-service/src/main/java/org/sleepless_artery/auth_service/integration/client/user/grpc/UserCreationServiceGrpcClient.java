package org.sleepless_artery.auth_service.integration.client.user.grpc;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.sleepless_artery.auth_service.CreateUserRequest;
import org.sleepless_artery.auth_service.UserCreationServiceGrpc;
import org.sleepless_artery.auth_service.common.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.auth_service.integration.client.user.UserCreationResult;
import org.sleepless_artery.auth_service.integration.client.user.UserCreationService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * gRPC client for communicating with the external user service.
 * Includes resilience patterns (CircuitBreaker, Retry).
 */
@Service
@RequiredArgsConstructor
public class UserCreationServiceGrpcClient implements UserCreationService {

    @GrpcClient(USER_CREATION_SERVICE_NAME)
    private UserCreationServiceGrpc.UserCreationServiceFutureStub futureStub;

    private static final String USER_CREATION_SERVICE_NAME = "user-service";


    /**
     * Calls the remote user service to create a user record.
     *
     * @param emailAddress the email of the user to create
     * @return UserCreationResult indicating success or failure
     * @throws ExternalServiceUnavailableException if the remote service is unreachable
     */
    @Override
    @CircuitBreaker(name = USER_CREATION_SERVICE_NAME, fallbackMethod = "fallbackUserCreation")
    @Retry(name = USER_CREATION_SERVICE_NAME)
    public UserCreationResult createUser(String emailAddress) {
        var request = CreateUserRequest.newBuilder()
                .setEmailAddress(emailAddress)
                .build();

        try {
            var response = futureStub
                    .withDeadlineAfter(30, TimeUnit.SECONDS)
                    .createUser(request)
                    .get()
                    .getSuccess();

            return response ? UserCreationResult.SUCCESS : UserCreationResult.FAILURE;
        } catch (Exception e) {
            throw new ExternalServiceUnavailableException("User service is unavailable");
        }
    }

    private UserCreationResult fallbackUserCreation(String emailAddress, Throwable throwable) {
        return UserCreationResult.SERVICE_UNAVAILABLE;
    }
}
