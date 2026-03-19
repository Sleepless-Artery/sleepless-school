package org.sleepless_artery.assignment_service.service.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.assignment_service.service.deletion.AssignmentDeletionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


/**
 * Kafka consumer for processing assignment-related events.
 */
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final AssignmentDeletionService deletionService;

    /**
     * Handles events indicating that a lesson has been deleted.
     * <p>
     * Deletes all assignments related to the lesson which ID is provided.
     *
     * @param lessonId deleted lesson ID
     */
    @KafkaListener(topics = "lesson.lessons.deleted")
    public void listenLessonDeletedEvent(Long lessonId) {
        deletionService.deleteAssignmentsByLessonId(lessonId);
    }
}
