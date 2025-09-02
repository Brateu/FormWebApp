package org.microservices.responseservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Response Service.
 * Loads environment variables from a .env file and initializes the Spring Boot application.
 */
@SpringBootApplication
public class ResponseServiceApplication {

  public static void main(String[] args) {
    // Load environment variables from .env file
    Dotenv dotenv = Dotenv.configure()
        .directory("./") // Look for .env in parent directory (project root)
        .ignoreIfMissing()
        .load();

    // Set system properties for all environment variables
    for (String key : dotenv.entries().stream().map(e -> e.getKey()).toArray(String[]::new)) {
      System.setProperty(key, dotenv.get(key));
    }

    SpringApplication.run(ResponseServiceApplication.class, args);
  }
}