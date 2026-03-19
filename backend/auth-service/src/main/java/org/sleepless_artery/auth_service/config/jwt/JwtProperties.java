package org.sleepless_artery.auth_service.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


/**
 * Configuration properties for JWT settings (secret and lifetime).
 * Bound to {@code jwt} prefix in application properties.
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter @Setter
public class JwtProperties {

    private String secret;

    private Duration lifetime;
}