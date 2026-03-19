package org.sleepless_artery.notification_service.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.sleepless_artery.notification_service.config.kafka.properties.KafkaConsumerConfigProperties;
import org.sleepless_artery.notification_service.service.infrastructure.kafka.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Kafka consumer configuration.
 *
 * <p>Creates and configures beans required for Kafka message consumption:
 * consumer factory and listener container factory</p>
 *
 * <p>Configuration values are provided via {@link KafkaConsumerConfigProperties}.</p>
 */
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaConsumerConfigProperties configProperties;


    /**
     * Creates Kafka {@link ConsumerFactory}.
     *
     * <p>Configures bootstrap servers, deserializers, consumer group,
     * offset strategy and JSON deserialization settings.</p>
     *
     * @return configured {@link ConsumerFactory}
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, configProperties.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, configProperties.getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, configProperties.getValueDeserializer());

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, configProperties.getAutoOffsetReset());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, configProperties.isEnableAutoCommit());

        return new DefaultKafkaConsumerFactory<>(props);
    }


    /**
     * Creates Kafka listener container factory used by {@link KafkaConsumer}.
     *
     * <p>Applies consumer factory and concurrency level.</p>
     *
     * @return configured {@link ConcurrentKafkaListenerContainerFactory}
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(configProperties.getConcurrency());
        return factory;
    }
}