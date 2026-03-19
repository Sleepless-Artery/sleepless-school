package org.sleepless_artery.auth_service.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Configuration for password encoding using BCrypt.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Creates a BCrypt password encoder with strength 12.
     *
     * @return PasswordEncoder bean
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
