package org.sleepless_artery.auth_service.email.service.event;


/**
 * Event publisher interface for sending email-related events to message broker.
 */
public interface EmailEventPublisher {

    void publishEmailConfirmation(String emailAddress, String topic);

    void publishEmailChanged(String oldEmailAddress, String newEmailAddress);
}