package org.sleepless_artery.user_service.service.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.user_service.service.email.EmailChangeService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;


/**
 * Kafka consumer for processing user-related events.
 */
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final EmailChangeService emailChangeService;

    /**
     * Listens to email-changed events and confirms email change.
     *
     * @param message new email
     * @param key user ID
     */
    @KafkaListener(topics = "auth.users.email-changed", groupId = "user-service")
    public void listenEmailChangedEvent(String message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        emailChangeService.confirmEmailAddressChange(key, message);
    }
}
