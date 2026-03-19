package org.sleepless_artery.notification_service.service.core;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.notification_service.exception.SendingEmailException;
import org.sleepless_artery.notification_service.logging.annotation.BusinessEvent;
import org.sleepless_artery.notification_service.logging.event.LogEvent;
import org.sleepless_artery.notification_service.model.EmailDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    @Override
    @BusinessEvent(LogEvent.SENDING_EMAIL)
    public void sendMail(EmailDetails emailDetails) {
        if (emailDetails == null || emailDetails.getRecipient() == null) {
            throw new IllegalArgumentException("Email details must not be null");
        }

        try {
            var message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(emailDetails.getRecipient());
            message.setSubject(emailDetails.getSubject());
            message.setText(emailDetails.getBody());

            mailSender.send(message);
        } catch (MailException e) {
            throw new SendingEmailException("Error sending message: " + e.getMessage());
        }
    }
}
