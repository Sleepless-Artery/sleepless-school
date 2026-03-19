package org.sleepless_artery.lesson_service.service.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.lesson_service.service.core.LessonService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


/**
 * Kafka consumer for processing lesson-related events.
 */
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final LessonService lessonService;


    /**
     * Handles events indicating that a course has been deleted.
     * <p>
     * Deletes all lessons related to the course which ID is provided.
     *
     * @param id deleted course ID
     */
    @KafkaListener(topics = "course.courses.deleted", groupId = "lesson-service")
    public void listenCourseDeletedEvent(Long id) {
        lessonService.deleteLessonsByCourseId(id);
    }
}
