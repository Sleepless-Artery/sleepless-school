package org.sleepless_artery.course_service.service.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.course_service.service.core.CourseService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;


/**
 * Kafka consumer for processing course-related events.
 */
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final CourseService courseService;

    /**
     * Handles events indicating that a user has been deleted.
     * <p>
     * Deletes all courses created by the user whose ID is provided.
     *
     * @param key deleted user ID
     */
    @KafkaListener(topics = "user.profiles.deleted", groupId = "course-service")
    public void listenUserDeletedEvent(@Header(KafkaHeaders.RECEIVED_KEY) String key) {
        courseService.deleteCoursesByAuthorId(Long.parseLong(key));
    }

    /**
     * Handles events indicating that a lesson has been updated or deleted.
     * <p>
     * Updates the last update date of the corresponding course.
     *
     * @param courseId ID of the course to update
     */
    @KafkaListener(topics = "lesson.lessons.updated", groupId = "course-service")
    public void listenLessonDeletedEvent(Long courseId) {
        courseService.updateCourseLastUpdateDate(courseId);
    }
}