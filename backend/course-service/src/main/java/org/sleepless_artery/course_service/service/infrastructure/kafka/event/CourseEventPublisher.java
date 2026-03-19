package org.sleepless_artery.course_service.service.infrastructure.kafka.event;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.course_service.service.infrastructure.kafka.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Publishes course-related domain events to Kafka.
 * <p>
 * Used to notify other microservices about changes in course lifecycle.
 */
@Component
@RequiredArgsConstructor
public class CourseEventPublisher {

    private final KafkaProducer kafkaProducer;

    @Value("${spring.kafka.topic.prefix}")
    private String kafkaTopicPrefix;

    @Value("${spring.kafka.topic.domain}")
    private String kafkaTopicDomain;

    private static final String COURSE_DELETED = "deleted";


    /**
     * Publishes an event indicating that a course has been deleted.
     *
     * @param id identifier of the deleted course
     */
    public void publishCourseDeletedEvent(Long id) {
        var topic = String.format(
                "%s.%s.%s",
                kafkaTopicPrefix,
                kafkaTopicDomain,
                COURSE_DELETED
        );

        kafkaProducer.send(topic, id);
    }
}