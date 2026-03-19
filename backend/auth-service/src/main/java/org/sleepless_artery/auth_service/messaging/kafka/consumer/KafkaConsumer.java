package org.sleepless_artery.auth_service.messaging.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.credential.service.core.CredentialService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


/**
 * Kafka consumer for processing user-related events.
 */
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final CredentialService credentialService;

    /**
     * Handles events indicating that a user has been deleted.
     *
     * @param message deleted user email address
     */
    @KafkaListener(topics = "user.profiles.deleted", groupId = "auth-service")
    public void listenUserDeletedEvent(String message) {
        credentialService.deleteByEmailAddress(message);
    }
}