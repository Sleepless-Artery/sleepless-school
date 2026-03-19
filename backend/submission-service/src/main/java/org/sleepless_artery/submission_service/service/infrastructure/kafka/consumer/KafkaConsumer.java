package org.sleepless_artery.submission_service.service.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.submission_service.service.deletion.SubmissionDeletionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;


/**
 * Kafka consumer for processing submission-related events.
 */
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final SubmissionDeletionService deletionService;


    /**
     * Handles events indicating that an assignment has been deleted.
     * <p>
     * Deletes all submissions related to the assignment which ID is provided.
     *
     * @param assignmentId deleted assignment ID
     */
    @KafkaListener(topics = "assignment.assignments.deleted")
    public void listenAssignmentDeletedEvent(Long assignmentId) {
        deletionService.deleteAllByAssignmentId(assignmentId);
    }


    /**
     * Handles events indicating that a user has been deleted.
     * <p>
     * Deletes all submissions related to the user whose ID is provided.
     *
     * @param key deleted user ID
     */
    @KafkaListener(topics = "user.profiles.deleted")
    public void listenUserDeletedEvent(@Header(KafkaHeaders.RECEIVED_KEY) String key) {
        deletionService.deleteAllByStudentId(Long.parseLong(key));
    }
}
