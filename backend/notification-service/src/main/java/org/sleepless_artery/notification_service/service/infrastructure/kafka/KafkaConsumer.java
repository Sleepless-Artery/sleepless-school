package org.sleepless_artery.notification_service.service.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.notification_service.model.EmailDetails;
import org.sleepless_artery.notification_service.service.core.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;


/**
 * Kafka consumer responsible for processing email events.
 *
 * <p>Listens to Kafka topics produced by the authentication service and sends
 * corresponding email notifications using {@link NotificationService}.</p>
 *
 * <p>Supported events:</p>
 * <ul>
 *     <li>User registration confirmation</li>
 *     <li>Email address change confirmation</li>
 *     <li>Password reset confirmation</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final NotificationService notificationService;

    private static final String CONFIRMATION_CODE = "Your confirmation code: ";
    private static final String REGISTRATION_CONFIRMATION_MESSAGE = "Your confirmation code: ";
    private static final String EMAIL_CONFIRMATION_MESSAGE = "Email confirmation";
    private static final String RESET_PASSWORD_CONFIRMATION_MESSAGE = "Reset password confirmation";


    /**
     * Consumes registration confirmation events.
     *
     * <p>Sends an email containing a confirmation code to verify
     * the user's email address during registration.</p>
     *
     * @param message confirmation code
     * @param key recipient email address
     */
    @KafkaListener(topics = "auth.users.check-email", groupId = "notification-service")
    public void listenCheckEmailEvent(String message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        processEmailEvent(key, REGISTRATION_CONFIRMATION_MESSAGE, CONFIRMATION_CODE + message);
    }


    /**
     * Consumes email change confirmation events.
     *
     * <p>Sends a confirmation code to verify the new email address.</p>
     *
     * @param message confirmation code
     * @param key recipient email address
     */
    @KafkaListener(topics = "auth.users.change-email-address", groupId = "notification-service")
    public void listenChangeEmailAddressEvent(String message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        processEmailEvent(key, EMAIL_CONFIRMATION_MESSAGE, CONFIRMATION_CODE + message);
    }


    /**
     * Consumes password reset events.
     *
     * <p>Sends a confirmation code required for password reset.</p>
     *
     * @param message confirmation code
     * @param key recipient email address
     */
    @KafkaListener(topics = "auth.users.reset-password", groupId = "notification-service")
    public void listenResetPasswordEvent(String message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        processEmailEvent(key, RESET_PASSWORD_CONFIRMATION_MESSAGE, CONFIRMATION_CODE + message);
    }


    /**
     * Sends an email notification using provided parameters.
     *
     * @param recipient recipient email address
     * @param subject email subject
     * @param body email body
     */
    private void processEmailEvent(String recipient, String subject, String body) {
        notificationService.sendMail(
                EmailDetails.builder()
                        .recipient(recipient)
                        .subject(subject)
                        .body(body)
                        .build()
        );
    }
}