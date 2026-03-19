package org.sleepless_artery.lesson_service.service.infrastructure.grpc.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.sleepless_artery.assignment_service.LessonVerificationServiceGrpc;
import org.sleepless_artery.assignment_service.VerifyLessonExistenceRequest;
import org.sleepless_artery.assignment_service.VerifyLessonExistenceResponse;
import org.sleepless_artery.lesson_service.service.core.LessonService;


/**
 * gRPC service for verifying lesson existence.
 * <p>
 * Used by external services to check whether
 * a lesson exists before performing operations that depend on it.
 */
@GrpcService
@RequiredArgsConstructor
public class LessonVerificationServiceImpl extends LessonVerificationServiceGrpc.LessonVerificationServiceImplBase {

    private final LessonService lessonService;

    /**
     * Verifies course existence.
     *
     * @param request gRPC lesson existence verification request
     * @param responseObserver response observer
     */
    @Override
    public void verifyLessonExistence(
            VerifyLessonExistenceRequest request,
            StreamObserver<VerifyLessonExistenceResponse> responseObserver
    ) {
        try {
            responseObserver.onNext(
                    VerifyLessonExistenceResponse.newBuilder()
                            .setExistence(lessonService.existsById(request.getLessonId()))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error verifying lesson existence")
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }
}
