package org.sleepless_artery.lesson_service.service.infrastructure.grpc.client.course;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.sleepless_artery.lesson_service.CourseVerificationServiceGrpc;
import org.sleepless_artery.lesson_service.VerifyCourseExistenceRequest;
import org.sleepless_artery.lesson_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.lesson_service.logging.annotation.BusinessEvent;
import org.sleepless_artery.lesson_service.logging.event.LogEvent;
import org.sleepless_artery.lesson_service.service.external.course.CourseExistenceChecker;
import org.sleepless_artery.lesson_service.service.external.course.CourseExistenceVerificationResult;
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

    @GrpcClient(COURSE_SERVICE_NAME)
    private CourseVerificationServiceGrpc.CourseVerificationServiceFutureStub futureStub;

    private static final String COURSE_SERVICE_NAME = "course-service";


    /**
     * Verifies course existence via gRPC request.
     *
     * @param courseId course identifier to verify
     * @return verification result
     * @throws ExternalServiceUnavailableException if service call fails
     */
    @Override
    @BusinessEvent(LogEvent.COURSE_VERIFICATION)
    @CircuitBreaker(name = COURSE_SERVICE_NAME, fallbackMethod = "fallbackCourseExistence")
    @Retry(name = COURSE_SERVICE_NAME)
    public CourseExistenceVerificationResult verifyCourseExistence(Long courseId) {
        var request = VerifyCourseExistenceRequest.newBuilder()
                .setId(courseId)
                .build();

        try {
            var response = futureStub
                    .withDeadlineAfter(1, TimeUnit.SECONDS)
                    .verifyCourseExistence(request)
                    .get(1500, TimeUnit.MILLISECONDS);

            return response.getExistence()
                    ? CourseExistenceVerificationResult.EXISTS
                    : CourseExistenceVerificationResult.NOT_FOUND;

        } catch (Exception e) {
            throw new ExternalServiceUnavailableException("Course service is unavailable");
        }
    }


    private CourseExistenceVerificationResult fallbackCourseExistence(Long courseId, Throwable throwable) {
        return CourseExistenceVerificationResult.SERVICE_UNAVAILABLE;
    }
}