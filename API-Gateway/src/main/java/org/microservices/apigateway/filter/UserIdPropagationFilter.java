package org.microservices.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.List;

/**
 * Global filter that extracts the user ID from JWT tokens in the security context and propagates it to downstream services.
 * This filter intercepts all authenticated requests, extracts the user ID from the JWT token in the security context,
 * and adds it as an X-User-ID header to the request before forwarding it to the target service.
 *
 * This filter works in conjunction with Spring Security's OAuth2 resource server configuration and
 * ensures that all authenticated requests to protected endpoints include the user's identity.
 * Public endpoints are excluded from this processing.
 */
@Component
public class UserIdPropagationFilter implements GlobalFilter, Ordered {
    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(UserIdPropagationFilter.class);

    /**
     * List of public endpoints that should bypass authentication and user ID propagation.
     * Requests to these endpoints will not have the X-User-ID header added.
     */
    private final List<String> publicEndpoints = Arrays.asList(
            "/api/user/register",
            "/api/user/login",
            "/api/v1/auth/oauth2",
            "/oauth2",
            "/api/forms/public",
            "/api/forms/public/"
    );

    /**
     * Filters incoming requests to extract and propagate user ID from JWT tokens.
     * This method:
     * 1. Checks if the request is for a public endpoint and skips processing if it is
     * 2. Verifies that a valid Authorization header is present
     * 3. Extracts the JWT token from the security context
     * 4. Retrieves the user ID from the JWT claims
     * 5. Adds the user ID as an X-User-ID header to the request
     *
     * @param exchange The current server exchange
     * @param chain The filter chain to delegate to once processing is complete
     * @return A Mono that completes when the filter chain has been executed
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.debug("Processing request for path: {}", path);

        // Skip filter for public endpoints
        if (publicEndpoints.stream().anyMatch(path::startsWith) || path.matches("/api/forms/public/\\d+")) {
            log.debug("Skipping filter for public endpoint: {}", path);
            return chain.filter(exchange);
        }

        // Get the Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        log.debug("Authorization header: {}", authHeader != null ? "present" : "missing");

        // If no Authorization header or it doesn't start with "Bearer ", continue without adding X-User-ID header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No valid Authorization header found, continuing without adding X-User-ID");
            return chain.filter(exchange);
        }

        log.debug("Attempting to extract JWT from security context");
        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(ctx -> log.debug("Security context found: {}", ctx != null))
                .map(SecurityContext::getAuthentication)
                .doOnNext(auth -> log.debug("Authentication found: {}, isAuthenticated: {}",
                        auth != null ? auth.getClass().getSimpleName() : "null",
                        auth != null ? auth.isAuthenticated() : "N/A"))
                .filter(auth -> auth != null && auth.isAuthenticated())
                .map(Authentication::getPrincipal)
                .doOnNext(principal -> log.debug("Principal found: {}, type: {}",
                        principal != null,
                        principal != null ? principal.getClass().getName() : "null"))
                .filter(principal -> principal instanceof Jwt)
                .cast(Jwt.class)
                .doOnNext(jwt -> log.debug("JWT found, claims: {}", jwt.getClaims().keySet()))
                .map(jwt -> {
                    // Get user ID from userId claim
                    Object userIdClaim = jwt.getClaim("userId");
                    log.debug("userId claim: {}", userIdClaim);

                    if (userIdClaim != null) {
                        String userId = userIdClaim.toString();
                        log.debug("Adding X-User-ID header with value: {}", userId);

                        // Add user ID to request headers
                        ServerHttpRequest request = exchange.getRequest().mutate()
                                .header("X-User-ID", userId)
                                .build();

                        return exchange.mutate().request(request).build();
                    }
                    log.warn("No userId claim found in JWT");
                    return exchange;
                })
                .defaultIfEmpty(exchange)
                .doOnNext(ex -> {
                    if (ex == exchange) {
                        log.debug("Using default exchange (no JWT processing occurred)");
                    }
                })
                .flatMap(chain::filter);
    }

    /**
     * Defines the order in which this filter is executed relative to other filters.
     * A lower order value means higher precedence.
     * This filter runs with high precedence but after the Spring Security filters.
     *
     * @return The order value for this filter
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

    /**
     * Extracts the user ID from the JWT token.
     * This method is used by the filter to get the user ID from the JWT claims.
     * It supports both numeric and string user IDs.
     *
     * @param jwt The JWT token
     * @return The user ID as a Long, or null if not found
     */
    Long extractUserId(Jwt jwt) {
        Object idClaim = jwt.getClaim("id");
        switch (idClaim) {
            case null -> {
                return null;
            }
            case Number number -> {
                return number.longValue();
            }
            case String s -> {
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse user ID from JWT: {}", idClaim);
                    return null;
                }
            }
            default -> {
            }
        }

        log.warn("Unexpected type for user ID in JWT: {}", idClaim.getClass().getName());
        return null;
    }
}