package org.microservices.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for password encoding.
 * Provides a password encoder bean for secure password handling throughout the application.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Creates and configures a password encoder bean.
     * Uses BCrypt hashing algorithm for password encryption.
     *
     * @return A configured BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}