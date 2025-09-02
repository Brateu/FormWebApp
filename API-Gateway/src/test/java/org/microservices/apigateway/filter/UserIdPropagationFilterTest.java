package org.microservices.apigateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserIdPropagationFilterTest {

    private UserIdPropagationFilter filter;

    @Mock
    private GatewayFilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new UserIdPropagationFilter();

        when(filterChain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void shouldExtractUserIdFromJwt() {
        // Given
        Long userId = 123L;
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);

        Jwt jwt = new Jwt(
            "token", 
            Instant.now(), 
            Instant.now().plusSeconds(300), 
            Map.of("alg", "HS256"), 
            claims
        );

        // When
        Long extractedUserId = filter.extractUserId(jwt);

        // Then
        assertEquals(userId, extractedUserId);
    }

    @Test
    void shouldHandleStringUserIdInJwt() {
        // Given
        String userId = "456";
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);

        Jwt jwt = new Jwt(
            "token", 
            Instant.now(), 
            Instant.now().plusSeconds(300), 
            Map.of("alg", "HS256"), 
            claims
        );

        // When
        Long extractedUserId = filter.extractUserId(jwt);

        // Then
        assertEquals(Long.parseLong(userId), extractedUserId);
    }

    @Test
    void shouldReturnNullWhenNoUserIdInJwt() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        // Add a dummy claim to avoid "claims cannot be empty" error
        claims.put("sub", "user123");
        // No id claim

        Jwt jwt = new Jwt(
            "token", 
            Instant.now(), 
            Instant.now().plusSeconds(300), 
            Map.of("alg", "HS256"), 
            claims
        );

        // When
        Long extractedUserId = filter.extractUserId(jwt);

        // Then
        assertEquals(null, extractedUserId);
    }
}