package org.microservices.userservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure()
                .directory("./User-Service")
                .load();

        // Set environment variables for Spring Boot
        for (String key : dotenv.entries().stream().map(e -> e.getKey()).toArray(String[]::new)) {
            System.setProperty(key, dotenv.get(key));
        }

        SpringApplication.run(UserServiceApplication.class, args);
    }

}