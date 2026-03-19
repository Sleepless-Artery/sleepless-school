package org.sleepless_artery.course_service.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.sleepless_artery.course_service.config.kafka.properties.KafkaConsumerConfigProperties;
import org.sleepless_artery.course_service.service.infrastructure.kafka.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;


/**
 * Kafka consumer configuration.
 *
 * <p>Creates and configures beans required for Kafka message consumption:
 * consumer factory, listener container factory, and error handler with
 * dead-letter topic support.</p>
 *
 * <p>Configuration values are provided via {@link KafkaConsumerConfigProperties}.</p>
 */
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaConsumerConfigProperties configProperties;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    /**
     * Creates Kafka {@link ConsumerFactory}.
     *
     * <p>Configures bootstrap servers, deserializers, consumer group,
     * offset strategy and JSON deserialization settings.</p>
     *
     * @return configured {@link ConsumerFactory}
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, configProperties.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, configProperties.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, configProperties.getValueDeserializer());

        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class.getName());
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, configProperties.getTrustedPackages());

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, configProperties.getAutoOffsetReset());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, configProperties.isEnableAutoCommit());

        return new DefaultKafkaConsumerFactory<>(props);
    }


    /**
     * Configures Kafka error handling strategy.
     *
     * <p>Messages that fail processing are retried several times.
     * After retry attempts are exhausted, the message is sent
     * to a Dead Letter Topic (DLT).</p>
     *
     * @return configured {@link DefaultErrorHandler}
     */
    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate),
                new FixedBackOff(1000L, 3)
        );
    }


    /**
     * Creates Kafka listener container factory used by {@link KafkaConsumer}.
     *
     * <p>Applies consumer factory, concurrency level,
     * and common error handler.</p>
     *
     * @return configured {@link ConcurrentKafkaListenerContainerFactory}
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        factory.setConcurrency(configProperties.getConcurrency());

        return factory;
    }
}