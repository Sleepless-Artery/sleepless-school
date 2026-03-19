package org.sleepless_artery.user_service.service.infrastructure.grpc.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.sleepless_artery.user_service.EmailAddressAvailabilityRequest;
import org.sleepless_artery.user_service.EmailVerificationServiceGrpc;

import org.sleepless_artery.user_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.user_service.logging.annotation.BusinessEvent;
import org.sleepless_artery.user_service.logging.event.LogEvent;
import org.sleepless_artery.user_service.service.external.email.EmailAvailabilityVerificationResult;
import org.sleepless_artery.user_service.service.external.email.EmailVerificationService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * gRPC client for email availability verification.
 * <p>
 * Communicates with external auth service and applies resiliency mechanisms.
 */
@Service
public class EmailVerificationServiceGrpcClient implements EmailVerificationService {

    @GrpcClient(AUTH_SERVICE)
    private EmailVerificationServiceGrpc.EmailVerificationServiceFutureStub futureStub;

    private static final String AUTH_SERVICE = "auth-service";


    /**
     * Verifies email address availability via auth service.
     *
     * @param emailAddress email address to verify
     * @return verification result
     * @throws ExternalServiceUnavailableException if service call fails
     */
    @Override
    @BusinessEvent(LogEvent.EMAIL_VERIFICATION)
    @CircuitBreaker(name = AUTH_SERVICE, fallbackMethod = "fallbackEmailAvailabilityVerification")
    @Retry(name = AUTH_SERVICE)
    public EmailAvailabilityVerificationResult verifyEmailAddressAvailability(String emailAddress) {

        var request = EmailAddressAvailabilityRequest.newBuilder()
                .setEmailAddress(emailAddress)
                .build();

        try {
            var response = futureStub
                    .withDeadlineAfter(1, TimeUnit.SECONDS)
                    .isEmailAddressAvailable(request)
                    .get(1500, TimeUnit.MILLISECONDS);

            return response.getAvailability()
                    ? EmailAvailabilityVerificationResult.AVAILABLE
                    : EmailAvailabilityVerificationResult.OCCUPIED;

        } catch (Exception e) {
            throw new ExternalServiceUnavailableException("Auth service is unavailable");
        }
    }


    private EmailAvailabilityVerificationResult fallbackEmailAvailabilityVerification(String emailAddress, Throwable t) {
        return EmailAvailabilityVerificationResult.SERVICE_UNAVAILABLE;
    }
}
