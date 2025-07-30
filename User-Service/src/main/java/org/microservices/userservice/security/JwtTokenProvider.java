package org.microservices.userservice.security;


import org.microservices.userservice.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.microservices.userservice.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;


@Component
/**
 * Provider class for JWT token generation and validation.
 * Handles the creation, parsing, and validation of JWT tokens used for authentication.
 * This class is responsible for encoding user details into tokens and extracting
 * information from tokens for authentication purposes.
 */
public class JwtTokenProvider {

    /**
     * The role of the user.
     */
    private Role role;

    /**
     * The secret key used for signing JWT tokens.
     */
    private final String jwtSecret;

    /**
     * The token expiration time in milliseconds.
     */
    private final long jwtExpirationInMs;

    /**
     * Parser for JWT tokens.
     */
    private final JwtParser jwtParser;

    /**
     * Constructs a new JwtTokenProvider with the specified parameters.
     *
     * @param jwtSecret The secret key used for signing JWT tokens
     * @param jwtExpirationInMs The token expiration time in milliseconds
     * @param userDetailsService Service for loading user-specific data
     */
    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String jwtSecret,
            @Value("${security.jwt.token-expiration}") long jwtExpirationInMs,
            UserDetailsService userDetailsService
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationInMs = jwtExpirationInMs;
        SecretKey key = Keys.hmacShaKeyFor(this.jwtSecret.getBytes());
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }

    /**
     * Generates a JWT token for the specified user.
     * The token includes the user's email as the subject, role, and user ID as claims.
     *
     * @param user The user for whom to generate the token
     * @return A JWT token string
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .claim("userId", user.getId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extracts the claims from a JWT token.
     *
     * @param token The JWT token to parse
     * @return The claims contained in the token
     */
    public Claims getClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }


    /**
     * Extracts the username (email) from a JWT token.
     * This method is kept for backward compatibility but is not used for validation.
     *
     * @param token The JWT token to parse
     * @return The username (email) contained in the token
     */
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

}