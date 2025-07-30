package org.microservices.apigateway.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converter that transforms JWT tokens into Spring Security authentication objects.
 * This class is responsible for extracting user roles from JWT claims and converting them
 * into Spring Security GrantedAuthority objects, which are then used for authorization decisions.
 * 
 * The converter supports both single role strings and lists of roles in the JWT claims.
 * It adds the "ROLE_" prefix to each role to conform to Spring Security's role-based authorization model.
 */
@Component
public class ReactiveJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    /**
     * The name of the claim in the JWT that contains the user's role(s).
     */
    private static final String ROLES_CLAIM = "role";

    /**
     * The prefix added to role names to conform to Spring Security's role-based authorization model.
     */
    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * Converts a JWT token into a Spring Security authentication token.
     * Extracts authorities (roles) from the JWT claims and creates a JwtAuthenticationToken.
     *
     * @param jwt The JWT token to convert
     * @return A Mono containing the created authentication token
     */
    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return Mono.just(new JwtAuthenticationToken(jwt, authorities, jwt.getSubject()));
    }

    /**
     * Extracts authorities (roles) from the JWT claims.
     * Supports both single role strings and lists of roles.
     * Adds the "ROLE_" prefix to each role.
     *
     * @param jwt The JWT token containing the claims
     * @return A collection of GrantedAuthority objects representing the user's roles
     */
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();

        if (claims.containsKey(ROLES_CLAIM)) {
            Object roles = claims.get(ROLES_CLAIM);
            if (roles instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> rolesList = (List<String>) roles;
                return rolesList.stream()
                        .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                        .collect(Collectors.toList());
            } else if (roles instanceof String roleString) {
                // Handle single role as string
                return Collections.singletonList(new SimpleGrantedAuthority(ROLE_PREFIX + roleString));
            }
        }

        return Collections.emptyList();
    }
}