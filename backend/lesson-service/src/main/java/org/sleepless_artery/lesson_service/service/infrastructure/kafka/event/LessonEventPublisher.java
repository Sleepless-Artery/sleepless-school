package org.sleepless_artery.lesson_service.service.infrastructure.kafka.event;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.lesson_service.service.infrastructure.kafka.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Publishes lesson-related events to Kafka.
 */
@Component
@RequiredArgsConstructor
public class LessonEventPublisher {

    private final KafkaProducer kafkaProducer;

    @Value("${spring.kafka.topic.prefix}")
    private String kafkaTopicPrefix;

    @Value("${spring.kafka.topic.domain}")
    private String kafkaTopicDomain;

    private static final String LESSON_UPDATED_TOPIC_SUFFIX = "updated";
    private static final String LESSON_DELETED_TOPIC_SUFFIX = "deleted";


    /**
     * Publishes a lesson update event.
     *
     * @param lessonId lesson identifier
     */
    public void publishLessonUpdatedEvent(Long lessonId) {
        publish(lessonId.toString(), LESSON_UPDATED_TOPIC_SUFFIX);
    }


    /**
     * Publishes a lesson deletion event.
     *
     * @param lessonId lesson identifier
     */
    public void publishLessonDeletedEvent(Long lessonId) {
        publish(lessonId.toString(), LESSON_DELETED_TOPIC_SUFFIX);
    }


    private void publish(String message, String kafkaTopicSuffix) {
        var topic = String.format(
                "%s.%s.%s",
                kafkaTopicPrefix,
                kafkaTopicDomain,
                kafkaTopicSuffix
        );

        kafkaProducer.send(topic, message);
    }
}
