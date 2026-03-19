package org.sleepless_artery.auth_service.email.service.event;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.auth_service.common.logging.annotation.BusinessEvent;
import org.sleepless_artery.auth_service.common.logging.event.LogEvent;
import org.sleepless_artery.auth_service.config.kafka.KafkaTopicConfig;
import org.sleepless_artery.auth_service.email.service.verification.VerificationService;
import org.sleepless_artery.auth_service.messaging.kafka.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * Kafka producer wrapper for sending email-related events.
 */
@Service
@RequiredArgsConstructor
public class EmailKafkaEventPublisher implements EmailEventPublisher {

    private final VerificationService verificationService;
    private final KafkaProducer kafkaProducer;

    @Value("${spring.kafka.topic.prefix}")
    private String prefix;

    @Value("${spring.kafka.topic.domain}")
    private String domain;


    /**
     * Publishes an email confirmation event to Kafka.
     *
     * @param emailAddress the target email address
     * @param topicPostfix the specific topic suffix (e.g., change_email, reset_password)
     */
    @Override
    @BusinessEvent(LogEvent.EMAIL_CONFIRMATION_SENDING)
    public void publishEmailConfirmation(String emailAddress, String topicPostfix) {
        var confirmationCode = verificationService.saveVerificationCode(emailAddress);

        kafkaProducer.send(
                String.format("%s.%s.%s", prefix, domain, topicPostfix),
                emailAddress,
                confirmationCode
        );
    }


    /**
     * Publishes an email changed event to Kafka.
     *
     * @param oldEmailAddress the previous email
     * @param newEmailAddress the new email
     */
    @Override
    public void publishEmailChanged(String oldEmailAddress, String newEmailAddress) {
        kafkaProducer.send(
                String.format("%s.%s.%s", prefix, domain, KafkaTopicConfig.EMAIL_CHANGED_TOPIC_NAME),
                oldEmailAddress,
                newEmailAddress
        );
    }
}
