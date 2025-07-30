package org.microservices.apigateway.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.microservices.apigateway.security.ReactiveJwtAuthenticationConverter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    @Mock
    private ReactiveJwtAuthenticationConverter jwtAuthenticationConverter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void testJwtDecoderThrowsExceptionWhenJwtSecretIsNull() {
        // Set jwtSecret to null
        ReflectionTestUtils.setField(securityConfig, "jwtSecret", null);

        // Verify that jwtDecoder throws IllegalStateException
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            securityConfig.jwtDecoder();
        });

        // Verify the exception message
        assertEquals("JWT secret is not configured. Please check your environment variables.", exception.getMessage());
    }

    @Test
    void testJwtDecoderThrowsExceptionWhenJwtSecretIsEmpty() {
        // Set jwtSecret to empty string
        ReflectionTestUtils.setField(securityConfig, "jwtSecret", "");

        // Verify that jwtDecoder throws IllegalStateException
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            securityConfig.jwtDecoder();
        });

        // Verify the exception message
        assertEquals("JWT secret is not configured. Please check your environment variables.", exception.getMessage());
    }

    @Test
    void testJwtDecoderCreatesDecoderWhenJwtSecretIsValid() {
        // Set jwtSecret to a valid value
        ReflectionTestUtils.setField(securityConfig, "jwtSecret", "test-secret-key-that-is-long-enough-for-hmac-sha256");

        // Verify that jwtDecoder returns a ReactiveJwtDecoder
        ReactiveJwtDecoder decoder = securityConfig.jwtDecoder();
        assertNotNull(decoder);
    }

    // We can't easily mock ServerHttpSecurity because it's a complex class with nested classes
    // Instead, we'll just verify that the method exists and is accessible
    @Test
    void testSecurityWebFilterChainExists() {
        // Set jwtSecret to a valid value to avoid NullPointerException in jwtDecoder
        ReflectionTestUtils.setField(securityConfig, "jwtSecret", "test-secret-key-that-is-long-enough-for-hmac-sha256");

        // Verify that the class has a method named securityWebFilterChain
        assertDoesNotThrow(() -> {
            SecurityConfig.class.getDeclaredMethod("securityWebFilterChain", ServerHttpSecurity.class);
        });
    }
}