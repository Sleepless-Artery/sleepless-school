package org.sleepless_artery.assignment_service.service.infrastructure.grpc.client.lesson;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.sleepless_artery.assignment_service.LessonVerificationServiceGrpc;
import org.sleepless_artery.assignment_service.VerifyLessonExistenceRequest;
import org.sleepless_artery.assignment_service.exception.ExternalServiceUnavailableException;
import org.sleepless_artery.assignment_service.logging.annotation.BusinessEvent;
import org.sleepless_artery.assignment_service.logging.event.LogEvent;
import org.sleepless_artery.assignment_service.service.external.lesson.LessonExistenceVerificationResult;
import org.sleepless_artery.assignment_service.service.external.lesson.LessonExistenceChecker;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * gRPC client for lesson existence verification.
 * <p>
 * Communicates with external lesson service and applies resiliency mechanisms.
 */
@Service
@RequiredArgsConstructor
public class LessonVerificationServiceGrpcClient implements LessonExistenceChecker {

    @GrpcClient(LESSON_SERVICE)
    private LessonVerificationServiceGrpc.LessonVerificationServiceFutureStub futureStub;

    private static final String LESSON_SERVICE = "lesson-service";


    /**
     * Verifies lesson existence via lesson service.
     *
     * @param lessonId lesson identifier to verify
     * @return verification result
     * @throws ExternalServiceUnavailableException if service call fails
     */
    @Override
    @BusinessEvent(LogEvent.LESSON_EXISTENCE_VERIFICATION)
    @CircuitBreaker(name = LESSON_SERVICE, fallbackMethod = "fallbackLessonExistence")
    @Retry(name = LESSON_SERVICE)
    public LessonExistenceVerificationResult verifyLessonExistence(Long lessonId) {
        var request = VerifyLessonExistenceRequest.newBuilder()
                .setLessonId(lessonId)
                .build();

        try {
            var response = futureStub
                    .withDeadlineAfter(1, TimeUnit.SECONDS)
                    .verifyLessonExistence(request)
                    .get(1500, TimeUnit.MILLISECONDS);

            return response.getExistence()
                    ? LessonExistenceVerificationResult.EXISTS
                    : LessonExistenceVerificationResult.NOT_FOUND;

        } catch (Exception e) {
            throw new ExternalServiceUnavailableException("Lesson service is unavailable");
        }
    }


    private LessonExistenceVerificationResult fallbackLessonExistence(Long lessonId, Throwable t) {
        return LessonExistenceVerificationResult.SERVICE_UNAVAILABLE;
    }
}
