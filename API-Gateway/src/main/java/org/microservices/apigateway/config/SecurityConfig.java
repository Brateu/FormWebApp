package org.microservices.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.microservices.apigateway.security.ReactiveJwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Security configuration for the API Gateway.
 * This class configures security settings for the gateway, including:
 * - CSRF protection
 * - Authorization rules for different endpoints
 * - JWT-based authentication
 * - JWT token validation
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * The secret key used for JWT token validation.
     * Injected from application properties.
     */
    @Value("${SECURITY_JWT_SECRET}")
    private String jwtSecret;

    /**
     * Converter for extracting authentication information from JWT tokens.
     */
    private ReactiveJwtAuthenticationConverter jwtAuthenticationConverter;

    /**
     * Sets the JWT authentication converter.
     * 
     * @param jwtAuthenticationConverter The converter to use for JWT authentication
     */
    @org.springframework.beans.factory.annotation.Autowired
    public void setJwtAuthenticationConverter(ReactiveJwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    /**
     * Configures the security filter chain for the API Gateway.
     * Defines which endpoints are publicly accessible and which require authentication.
     * Sets up JWT-based authentication for protected endpoints.
     *
     * @param http The ServerHttpSecurity to configure
     * @return The configured SecurityWebFilterChain
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/api/user/register",
                                "/api/user/login",
                                "/api/forms/{id}",
                                "/api/v1/auth/oauth2/**",
                                "/oauth2/**",
                                "/api/v1/auth/oauth2/success",
                                "/login/**"  ,
                                "/api/forms/public",
                                "/api/forms/public/**"
                        ).permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/responses").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("POST", "GET", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Creates and configures a JWT decoder for validating JWT tokens.
     * Uses the configured JWT secret to create a key for token validation.
     * Applies standard JWT validators to ensure token integrity and validity.
     *
     * @return A configured ReactiveJwtDecoder
     * @throws IllegalStateException if the JWT secret is not configured or if there's an error creating the decoder
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT secret is not configured. Please check your environment variables.");
        }

        try {
            // Create a simple key from the JWT secret
            SecretKeySpec key = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8), 
                0, 
                jwtSecret.getBytes(StandardCharsets.UTF_8).length, 
                "HmacSHA512"
            );

            // Create the JWT decoder with the key
            NimbusReactiveJwtDecoder jwtDecoder = NimbusReactiveJwtDecoder.withSecretKey(key)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();

            // Add validators
            OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefault();
            jwtDecoder.setJwtValidator(defaultValidators);

            return jwtDecoder;
        } catch (Exception e) {
            // If there's an error, throw an exception instead of using a mock decoder
            throw new IllegalStateException("Error creating JWT decoder: " + e.getMessage(), e);
        }
    }
}