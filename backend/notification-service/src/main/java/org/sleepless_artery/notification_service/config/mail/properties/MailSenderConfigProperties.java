package org.sleepless_artery.notification_service.config.mail.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;


/**
 * Configuration properties for mail sender.
 *
 * <p>Binds SMTP configuration from {@code spring.mail.*} properties
 * and validates required parameters for email delivery.</p>
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Getter @Setter
public class MailSenderConfigProperties {

    @NotBlank(message = "Mail host must be defined")
    private String host;

    @NotNull(message = "Mail port must be defined")
    private int port;

    @NotBlank(message = "Sender username must be defined")
    private String username;

    @NotBlank(message = "Mail password must be defined")
    private String password;

    @NotBlank(message = "Protocol must be defined")
    private String protocol;

    @NotNull(message = "Connection timeout must be defined")
    private int connectionTimeout;

    @NotNull(message = "Timeout must be defined")
    private int timeout;

    @NotNull(message = "Writing timeout must be defined")
    private int writeTimeout;
}
