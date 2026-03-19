package org.sleepless_artery.enrollment_service.service.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.enrollment_service.service.core.EnrollmentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;


/**
 * Kafka consumer for processing enrollment-related events.
 */
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final EnrollmentService enrollmentService;

    /**
     * Handles events indicating that a user has been deleted.
     * <p>
     * Deletes all enrollments of the user whose ID is provided.
     *
     * @param key deleted user ID
     */
    @KafkaListener(topics = "user.profiles.deleted", groupId = "enrollment-service")
    public void listenUserDeletedEvent(@Header(KafkaHeaders.RECEIVED_KEY) String key) {
        enrollmentService.deleteEnrollmentsByStudentId(Long.parseLong(key));
    }

    /**
     * Handles events indicating that a course has been deleted.
     * <p>
     * Deletes all enrollments related to the course which ID is provided.
     *
     * @param courseId deleted course ID
     */
    @KafkaListener(topics = "course.courses.deleted", groupId = "enrollment-service")
    public void listenCourseDeletedEvent(Long courseId) {
        enrollmentService.deleteEnrollmentsByCourseId(courseId);
    }
}
