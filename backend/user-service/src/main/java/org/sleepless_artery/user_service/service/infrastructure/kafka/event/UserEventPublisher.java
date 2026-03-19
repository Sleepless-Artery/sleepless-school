package org.sleepless_artery.user_service.service.infrastructure.kafka.event;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.user_service.service.infrastructure.kafka.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Publishes user-related events to Kafka.
 */
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final KafkaProducer kafkaProducer;

    @Value("${spring.kafka.topic.prefix}")
    private String kafkaTopicPrefix;

    @Value("${spring.kafka.topic.domain}")
    private String kafkaTopicDomain;

    private static final String USER_DELETED = "deleted";

    /**
     * Publishes a user deletion event.
     *
     * @param userId user identifier
     * @param email user email
     */
    public void publishUserDeletedEvent(Long userId, String email) {
        var topic = String.format(
                "%s.%s.%s",
                kafkaTopicPrefix,
                kafkaTopicDomain,
                USER_DELETED
        );

        kafkaProducer.send(topic, userId.toString(), email);
    }
}