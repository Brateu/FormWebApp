package org.microservices.formservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for WebClient to communicate with User-Service.
 * This class creates and configures a WebClient bean that will be used
 * to make HTTP requests to the User-Service.
 */
@Configuration
public class WebClientConfig {

    @Value("${user-service.url}")
    private String userServiceUrl;

    /**
     * Creates a WebClient bean configured with the base URL of the User-Service.
     * This bean can be injected into services that need to communicate with the User-Service.
     *
     * @return a configured WebClient instance
     */
    @Bean
    public WebClient userServiceWebClient() {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }
}