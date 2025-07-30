package org.microservices.userservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the User Service.
 * This service handles user management, authentication, and authorization.
 */
@SpringBootApplication
public class UserServiceApplication {

    /**
     * The main method that starts the User Service application.
     * Loads environment variables from a .env file and initializes the Spring Boot application.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory("./User-Service")
                .load();

        for (String key : dotenv.entries().stream().map(e -> e.getKey()).toArray(String[]::new)) {
            System.setProperty(key, dotenv.get(key));
        }

        SpringApplication.run(UserServiceApplication.class, args);
    }

}