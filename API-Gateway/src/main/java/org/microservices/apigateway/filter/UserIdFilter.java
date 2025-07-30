package org.microservices.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Filter that extracts the user ID from JWT tokens and adds it as a header to the request.
 * This filter intercepts all incoming requests, checks for a valid JWT token in the Authorization header,
 * extracts the user ID from the token claims, and adds it as an X-User-ID header to the request.
 * This allows downstream services to identify the authenticated user without having to parse the JWT token again.
 */
@Component
public class UserIdFilter implements WebFilter {
    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(UserIdFilter.class);

    /**
     * The secret key used for JWT token validation.
     * Injected from application properties.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Filters incoming requests to extract user ID from JWT tokens.
     * If a valid JWT token is present in the Authorization header, this method:
     * 1. Extracts the token from the header
     * 2. Validates the token using the JWT secret
     * 3. Extracts the user ID from the token claims
     * 4. Adds the user ID as an X-User-ID header to the request
     *
     * @param exchange The current server exchange
     * @param chain The filter chain to delegate to once processing is complete
     * @return A Mono that completes when the filter chain has been executed
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                SecretKeySpec key = new SecretKeySpec(
                    jwtSecret.getBytes(StandardCharsets.UTF_8),
                    0,
                    jwtSecret.getBytes(StandardCharsets.UTF_8).length,
                    "HmacSHA512"
                );

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // Extract user ID from claims
                Object userIdObj = claims.get("userId");
                if (userIdObj != null) {
                    String userId = userIdObj.toString();
                    log.debug("Extracted userId from token: {}", userId);

                    // Add X-User-ID header
                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header("X-User-ID", userId)
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                } else {
                    log.warn("No userId claim found in token");
                }
            } catch (Exception e) {
                // Log error but continue without adding header
                log.error("Error processing JWT token", e);
            }
        }

        return chain.filter(exchange);
    }
}