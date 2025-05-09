package org.microservices.formservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.microservices.formservice.mappers")

public class FormServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(FormServiceApplication.class, args);
  }
}