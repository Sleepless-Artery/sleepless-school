package org.sleepless_artery.assignment_service.service.infrastructure.kafka.producer;

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
     * Sends a message to Kafka topic.
     *
     * @param topic Kafka topic
     * @param message message to sent
     */
    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
