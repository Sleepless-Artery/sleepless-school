package org.sleepless_artery.user_service.service.infrastructure.grpc.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.sleepless_artery.user_service.UserVerificationServiceGrpc;
import org.sleepless_artery.user_service.VerifyUserExistenceRequest;
import org.sleepless_artery.user_service.VerifyUserExistenceResponse;
import org.sleepless_artery.user_service.service.core.UserService;


/**
 * gRPC service for verifying user existence.
 * <p>
 * Used by external services to check whether
 * a user exists before performing operations that depend on it.
 */
@GrpcService
@RequiredArgsConstructor
public class UserVerificationServiceImpl extends UserVerificationServiceGrpc.UserVerificationServiceImplBase {

    private final UserService userService;


    /**
     * Verifies course existence.
     *
     * @param request gRPC lesson existence verification request
     * @param responseObserver response observer
     */
    @Override
    public void verifyUserExistence(
            VerifyUserExistenceRequest request,
            StreamObserver<VerifyUserExistenceResponse> responseObserver
    ) {
        try {
            responseObserver.onNext(
                    VerifyUserExistenceResponse.newBuilder()
                            .setExistence(userService.existsById(request.getUserId()))
                            .build()
            );

            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error verifying user's existence")
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }
}
