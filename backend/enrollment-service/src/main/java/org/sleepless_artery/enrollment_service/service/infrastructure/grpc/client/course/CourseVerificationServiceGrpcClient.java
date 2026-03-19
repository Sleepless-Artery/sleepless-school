package org.sleepless_artery.enrollment_service.service.infrastructure.grpc.client.course;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.sleepless_artery.enrollment_service.CourseVerificationServiceGrpc;
import org.sleepless_artery.enrollment_service.VerifyCourseExistenceRequest;
import org.sleepless_artery.enrollment_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.enrollment_service.logging.annotation.BusinessEvent;
import org.sleepless_artery.enrollment_service.logging.event.LogEvent;
import org.sleepless_artery.enrollment_service.service.external.course.CourseExistenceChecker;
import org.sleepless_artery.enrollment_service.service.external.course.CourseExistenceVerificationResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * gRPC client for course existence verification.
 * <p>
 * Communicates with external course service and applies resiliency mechanisms.
 */
@Service
@RequiredArgsConstructor
public class CourseVerificationServiceGrpcClient implements CourseExistenceChecker {

    @GrpcClient(COURSE_VERIFICATION_SERVICE_NAME)
    private CourseVerificationServiceGrpc.CourseVerificationServiceFutureStub futureStub;

    private static final String COURSE_VERIFICATION_SERVICE_NAME = "course-service";


    /**
     * Verifies course existence via course service.
     *
     * @param courseId course identifier to verify
     * @return verification result
     * @throws ExternalServiceUnavailableException if service call fails
     */
    @Override
    @BusinessEvent(LogEvent.COURSE_VERIFICATION)
    @CircuitBreaker(name = COURSE_VERIFICATION_SERVICE_NAME, fallbackMethod = "fallbackCourseVerification")
    @Retry(name = COURSE_VERIFICATION_SERVICE_NAME)
    public CourseExistenceVerificationResult verifyCourseExistence(Long courseId) {
        var request = VerifyCourseExistenceRequest.newBuilder()
                .setId(courseId)
                .build();

        try {
            var result = futureStub
                    .withDeadlineAfter(1, TimeUnit.SECONDS)
                    .verifyCourseExistence(request)
                    .get(1500, TimeUnit.MILLISECONDS);

            return result.getExistence()
                    ? CourseExistenceVerificationResult.EXISTS
                    : CourseExistenceVerificationResult.NOT_FOUND;

        } catch (Exception e) {
            throw new ExternalServiceUnavailableException("Course service is unavailable");
        }
    }

    private CourseExistenceVerificationResult fallbackCourseVerification(Long courseId, Throwable throwable) {
        return CourseExistenceVerificationResult.SERVICE_UNAVAILABLE;
    }
}
