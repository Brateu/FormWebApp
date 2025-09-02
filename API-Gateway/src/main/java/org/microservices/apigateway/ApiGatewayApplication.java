package org.microservices.apigateway;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the API Gateway.
 * This gateway serves as the entry point for all client requests and routes them to the appropriate microservices.
 * It handles cross-cutting concerns such as authentication, authorization, logging, and request routing.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    /**
     * The main method that starts the API Gateway application.
     * Loads environment variables from a .env file and initializes the Spring Boot application.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory("./") // Look for .env in parent directory (project root)
                .ignoreIfMissing()
                .load();

        for (String key : dotenv.entries().stream().map(e -> e.getKey()).toArray(String[]::new)) {
            System.setProperty(key, dotenv.get(key));
        }

        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}