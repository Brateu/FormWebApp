package org.microservices.userservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * Main application class for the User Service.
 */
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        // Load .env file from root (or fallback)
        Dotenv dotenv = Dotenv.configure()
                .directory("./") // root folder
                .ignoreIfMissing()
                .load();

        // Prepare env map for Spring Boot
        Map<String, Object> envVariables = new HashMap<>();

        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();

            // Support both original and Spring-style keys
            String springKey = key.toLowerCase().replace('_', '.');

            System.setProperty(key, value);
            System.setProperty(springKey, value);

            envVariables.put(key, value);
            envVariables.put(springKey, value);
        });

        // Start app with loaded env
        SpringApplication app = new SpringApplication(UserServiceApplication.class);
        app.setEnvironment(createEnvironment(envVariables));
        app.run(args);
    }

    private static ConfigurableEnvironment createEnvironment(Map<String, Object> envVariables) {
        ConfigurableEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envVariables));
        return environment;
    }
}