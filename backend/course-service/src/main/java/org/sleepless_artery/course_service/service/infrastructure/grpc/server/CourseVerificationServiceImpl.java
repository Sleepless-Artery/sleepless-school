package org.sleepless_artery.course_service.service.infrastructure.grpc.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.sleepless_artery.course_service.CourseVerificationServiceGrpc;
import org.sleepless_artery.course_service.VerifyCourseExistenceRequest;
import org.sleepless_artery.course_service.VerifyCourseExistenceResponse;
import org.sleepless_artery.course_service.service.core.CourseService;


/**
 * gRPC service for verifying course existence.
 * <p>
 * Used by external services to check whether
 * a course exists before performing operations that depend on it.
 */
@GrpcService
@RequiredArgsConstructor
public class CourseVerificationServiceImpl extends CourseVerificationServiceGrpc.CourseVerificationServiceImplBase {

    private final CourseService courseService;

    /**
     * Verifies course existence.
     *
     * @param request gRPC lesson existence verification request
     * @param streamObserver response observer
     */
    @Override
    public void verifyCourseExistence(
            VerifyCourseExistenceRequest request,
            StreamObserver<VerifyCourseExistenceResponse> streamObserver
    ) {
        try {
            streamObserver.onNext(
                    VerifyCourseExistenceResponse.newBuilder()
                            .setExistence(courseService.existsById(request.getId()))
                            .build()
            );
            streamObserver.onCompleted();
        } catch (Exception e) {
            streamObserver.onError(Status.INTERNAL
                    .withDescription("Error verifying course existence")
                    .withCause(e)
                    .asRuntimeException()
            );
        }
    }
}
