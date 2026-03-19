package org.sleepless_artery.user_service.service.infrastructure.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


/**
 * Kafka producer for sending messages to topics.
 */
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Sends a message to Kafka topic with key and value.
     *
     * @param topic Kafka topic
     * @param key message key
     * @param value message value
     */
    public void send(String topic, String key, String value) {
        kafkaTemplate.send(topic, key, value);
    }
}