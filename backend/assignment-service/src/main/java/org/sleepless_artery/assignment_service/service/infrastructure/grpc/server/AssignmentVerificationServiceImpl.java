package org.sleepless_artery.assignment_service.service.infrastructure.grpc.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.sleepless_artery.assignment_service.service.query.AssignmentQueryService;
import org.sleepless_artery.assignment_service.AssignmentVerificationServiceGrpc;
import org.sleepless_artery.assignment_service.VerifyAssignmentExistenceRequest;
import org.sleepless_artery.assignment_service.VerifyAssignmentExistenceResponse;


/**
 * gRPC service for assignment existence verification.
 * <p>
 * Provides remote procedure calls for checking assignment existence across services.
 * </p>
 */
@GrpcService
@RequiredArgsConstructor
public class AssignmentVerificationServiceImpl extends AssignmentVerificationServiceGrpc.AssignmentVerificationServiceImplBase {

    private final AssignmentQueryService queryService;


    /**
     * Verifies if an assignment exists by its ID.
     *
     * @param request contains assignment ID to verify
     * @param responseObserver returns existence status or error
     */
    @Override
    public void verifyAssignmentExistence(
            VerifyAssignmentExistenceRequest request, StreamObserver<VerifyAssignmentExistenceResponse> responseObserver
    ) {
        try {
            responseObserver.onNext(
                    VerifyAssignmentExistenceResponse.newBuilder()
                            .setExistence(queryService.existsById(request.getAssignmentId()))
                            .build()
            );

            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error verifying assignment existence")
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }
}
