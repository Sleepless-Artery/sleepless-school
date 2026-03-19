package org.sleepless_artery.auth_service.integration.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.sleepless_artery.auth_service.EmailAddressAvailabilityRequest;
import org.sleepless_artery.auth_service.EmailAddressAvailabilityResponse;
import org.sleepless_artery.auth_service.EmailVerificationServiceGrpc;
import org.sleepless_artery.auth_service.email.service.reservation.EmailReservationService;


/**
 * gRPC server implementation for email verification checks.
 */
@GrpcService
@RequiredArgsConstructor
public class EmailVerificationServiceImpl extends EmailVerificationServiceGrpc.EmailVerificationServiceImplBase {

    private final EmailReservationService emailReservationService;


    /**
     * gRPC method to check if an email address is available.
     *
     * @param request the request containing the email address
     * @param streamObserver the observer to send the response
     */
    @Override
    public void isEmailAddressAvailable(
            EmailAddressAvailabilityRequest request, StreamObserver<EmailAddressAvailabilityResponse> streamObserver
    ) {
        var isAvailable = emailReservationService.isEmailAddressAvailable(request.getEmailAddress());

        try {
            streamObserver.onNext(EmailAddressAvailabilityResponse.newBuilder()
                    .setAvailability(isAvailable)
                    .build());

            streamObserver.onCompleted();
        } catch (Exception e) {
            streamObserver.onError(Status.INTERNAL
                    .withDescription("Error checking email availability")
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }
}