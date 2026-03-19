package org.sleepless_artery.lesson_service.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.sleepless_artery.lesson_service.config.kafka.properties.KafkaTopicConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


/**
 * Kafka topic configuration for the user domain.
 *
 * <p>Responsible for creating Kafka topics used by the lesson-service.
 * Topic names are constructed using the following pattern:</p>
 *
 * <pre>
 * {prefix}.{domain}.{event}
 * </pre>
 *
 * <p>Example:</p>
 * <pre>
 * lesson.lessons.deleted
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
     * Creates Kafka topic for lesson update events.
     *
     * @return configured {@link NewTopic} instance
     */
    @Bean
    public NewTopic lessonUpdatedTopic() {
        return createTopic("updated");
    }

    /**
     * Creates Kafka topic for lesson deletion events.
     *
     * @return configured {@link NewTopic} instance
     */
    @Bean
    public NewTopic lessonDeletedTopic() {
        return createTopic("deleted");
    }


    /**
     * Creates a Kafka topic using the configured naming convention.
     *
     * @param suffix event type suffix (e.g. deleted, created, updated)
     * @return configured {@link NewTopic}
     */
    private NewTopic createTopic(String suffix) {
        return TopicBuilder
                .name(String.format("%s.%s.%s",
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