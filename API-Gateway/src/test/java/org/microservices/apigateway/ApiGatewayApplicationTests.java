package org.microservices.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = {
    "jwt.secret=testsecret123456789",
    "jwt.token-expiration=3600000"
})
class ApiGatewayApplicationTests {

  @Test
  void contextLoads() {
    // This test just loads the application context
    System.out.println("Context loaded successfully");
  }
}