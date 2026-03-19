package org.sleepless_artery.notification_service.service.core;

import org.sleepless_artery.notification_service.model.EmailDetails;


/**
 * Notification management service.
 * <p>
 * Provides operation for sending emails.
 */
public interface NotificationService {
    void sendMail(EmailDetails emailDetails);
}
