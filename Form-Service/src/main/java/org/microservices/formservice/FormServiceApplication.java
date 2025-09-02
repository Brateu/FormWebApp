package org.microservices.formservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for the Form Service.
 * This service handles form creation, management, and collaboration.
 * It uses Feign clients for communication with other microservices.
 */
@SpringBootApplication
@EnableFeignClients
public class FormServiceApplication {

  /**
   * The main method that starts the Form Service application.
   * Loads environment variables from a .env file and initializes the Spring Boot application.
   *
   * @param args Command line arguments passed to the application
   */
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure()
            .directory("../") // Look for .env in parent directory (project root)
            .ignoreIfMissing()
            .load();

    for (String key : dotenv.entries().stream().map(e -> e.getKey()).toArray(String[]::new)) {
        System.setProperty(key, dotenv.get(key));
    }

    SpringApplication.run(FormServiceApplication.class, args);
  }
}