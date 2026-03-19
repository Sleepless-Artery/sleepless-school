package org.sleepless_artery.course_service.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.sleepless_artery.course_service.config.kafka.properties.KafkaTopicConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


/**
 * Kafka topic configuration for the course domain.
 *
 * <p>Responsible for creating Kafka topics used by the course-service.
 * Topic names are constructed using the following pattern:</p>
 *
 * <pre>
 * {prefix}.{domain}.{event}
 * </pre>
 *
 * <p>Example:</p>
 * <pre>
 * course.courses.deleted
 * </pre>
 *
 * <p>Topic parameters such as partitions, replication factor and
 * minimum in-sync replicas are configured via
 * {@link KafkaTopicConfigProperties}.</p>
 */
@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final KafkaTopicConfigProperties topicConfigProperties;


    /**
     * Creates Kafka topic for course deletion events.
     *
     * @return configured {@link NewTopic} instance
     */
    @Bean
    public NewTopic courseDeletedTopic() {
        return createTopic("deleted");
    }


    private NewTopic createTopic(String suffix) {
        return TopicBuilder.name(
                String.format("%s.%s.%s",
                        topicConfigProperties.getPrefix(), topicConfigProperties.getDomain(), suffix)
                )
                .partitions(topicConfigProperties.getPartitions())
                .replicas(topicConfigProperties.getReplicas())
                .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG,
                        topicConfigProperties.getMinInsyncReplicas().toString()
                )
                .build();
    }
}