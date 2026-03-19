package org.sleepless_artery.notification_service.config.mail;

import lombok.RequiredArgsConstructor;
import org.sleepless_artery.notification_service.config.mail.properties.MailSenderConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


/**
 * Mail sender configuration.
 *
 * <p>Creates and configures a {@link JavaMailSender} bean using
 * {@link MailSenderConfigProperties}.</p>
 *
 * <p>The configured sender supports SMTP authentication,
 * STARTTLS encryption and configurable timeouts.</p>
 */
@Configuration
@RequiredArgsConstructor
public class MailSenderConfig {

    private final MailSenderConfigProperties mailSenderConfigProperties;


    /**
     * Creates a configured {@link JavaMailSender} instance.
     *
     * @return configured mail sender
     */
    @Bean
    public JavaMailSender mailSender() {
        var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailSenderConfigProperties.getHost());
        mailSender.setPort(mailSenderConfigProperties.getPort());
        mailSender.setUsername(mailSenderConfigProperties.getUsername());
        mailSender.setPassword(mailSenderConfigProperties.getPassword());

        var props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailSenderConfigProperties.getProtocol());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectionTimeout", mailSenderConfigProperties.getConnectionTimeout());
        props.put("mail.smtp.timeout", mailSenderConfigProperties.getTimeout());
        props.put("mail.smtp.writeTimeout", mailSenderConfigProperties.getWriteTimeout());

        return mailSender;
    }
}
