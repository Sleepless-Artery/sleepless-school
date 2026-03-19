package org.sleepless_artery.auth_service.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.sleepless_artery.auth_service.config.kafka.properties.KafkaTopicConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


/**
 * Kafka topic configuration for the auth domain.
 *
 * <p>Responsible for creating Kafka topics used by the auth-service.
 * Topic names are constructed using the following pattern:</p>
 *
 * <pre>
 * {prefix}.{domain}.{event}
 * </pre>
 *
 * <p>Example:</p>
 * <pre>
 * auth.users.check-email
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

    public static final String CHECK_EMAIL_TOPIC_NAME = "check-email";
    public static final String CHANGE_EMAIL_TOPIC_NAME = "change-email-address";
    public static final String RESET_PASSWORD_TOPIC_NAME = "reset-password";
    public static final String EMAIL_CHANGED_TOPIC_NAME = "email-changed";


    /**
     * Creates Kafka topic for email verification events.
     *
     * @return configured {@link NewTopic} instance
     */
    @Bean
    public NewTopic checkEmailTopic() {
        return createTopic(CHECK_EMAIL_TOPIC_NAME);
    }


    /**
     * Creates Kafka topic for email changing events.
     *
     * @return configured {@link NewTopic} instance
     */
    @Bean
    public NewTopic changeEmailAddressTopic() {
        return createTopic(CHANGE_EMAIL_TOPIC_NAME);
    }


    /**
     * Creates Kafka topic for password reset events.
     *
     * @return configured {@link NewTopic} instance
     */
    @Bean
    public NewTopic resetPasswordTopic() {
        return createTopic(RESET_PASSWORD_TOPIC_NAME);
    }


    /**
     * Creates Kafka topic for events indicating that email address has been changed.
     *
     * @return configured {@link NewTopic} instance
     */
    @Bean
    public NewTopic emailChangedTopic() {
        return createTopic(EMAIL_CHANGED_TOPIC_NAME);
    }


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
