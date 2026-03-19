package org.sleepless_artery.notification_service.model;

import lombok.Builder;
import lombok.Getter;


/**
 * Email message details.
 *
 * <p>Represents the content of an email to be sent
 * including recipient, subject and message body.</p>
 */
@Builder
@Getter
public class EmailDetails {

    private String recipient;

    private String subject;

    private String body;
}
