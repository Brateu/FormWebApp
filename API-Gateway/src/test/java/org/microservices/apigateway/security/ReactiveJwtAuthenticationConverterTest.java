package org.microservices.apigateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ReactiveJwtAuthenticationConverterTest {

    private ReactiveJwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ReactiveJwtAuthenticationConverter();
    }

    @Test
    void testConvertWithSingleRoleAsString() {
        // Create a JWT with a single role as string
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claims(c -> c.putAll(claims))
                .build();

        // Convert the JWT
        Mono<AbstractAuthenticationToken> result = converter.convert(jwt);

        // Verify the result
        AbstractAuthenticationToken token = result.block();
        assertNotNull(token);
        assertTrue(token instanceof JwtAuthenticationToken);

        // Verify the authorities
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        // Verify the principal
        assertEquals("user", token.getName());
    }

    @Test
    void testConvertWithMultipleRolesAsList() {
        // Create a JWT with multiple roles as list
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = Arrays.asList("ADMIN", "USER");
        claims.put("role", roles);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claims(c -> c.putAll(claims))
                .build();

        // Convert the JWT
        Mono<AbstractAuthenticationToken> result = converter.convert(jwt);

        // Verify the result
        AbstractAuthenticationToken token = result.block();
        assertNotNull(token);
        assertTrue(token instanceof JwtAuthenticationToken);

        // Verify the authorities
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        // Verify the principal
        assertEquals("user", token.getName());
    }

    @Test
    void testConvertWithNoRoles() {
        // Create a JWT with no roles
        Map<String, Object> claims = new HashMap<>();

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claims(c -> c.putAll(claims))
                .build();

        // Convert the JWT
        Mono<AbstractAuthenticationToken> result = converter.convert(jwt);

        // Verify the result
        AbstractAuthenticationToken token = result.block();
        assertNotNull(token);
        assertTrue(token instanceof JwtAuthenticationToken);

        // Verify the authorities
        Collection<GrantedAuthority> authorities = token.getAuthorities();
        assertTrue(authorities.isEmpty());

        // Verify the principal
        assertEquals("user", token.getName());
    }
}