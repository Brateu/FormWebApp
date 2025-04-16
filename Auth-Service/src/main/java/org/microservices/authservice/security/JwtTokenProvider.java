package org.microservices.authservice.security;


import org.microservices.authservice.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.microservices.authservice.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JwtTokenProvider is a utility class responsible for generating, validating, and parsing
 * JWT tokens for authentication and authorization purposes within the application.
 *
 * This class provides methods to:
 * - Generate a JWT for a given user entity with embedded claims such as email and role.
 * - Extract the username (email) from a given JWT token.
 * - Validate the authenticity and correctness of a given JWT token.
 * - Parse claims from a JWT token for further processing.
 *
 * The class utilizes a secret key and token expiration duration, which are defined
 * in the application's configuration properties for secure token generation and validation.
 *
 * Key Responsibilities:
 * - Generate secure JWTs with expiration and embedded claims.
 * - Ensure the provided JWT tokens are valid and not tampered with.
 * - Extract claims such as username and roles for authenticated operations.
 */
@Component
public class JwtTokenProvider {

    private Role role;
    private final String jwtSecret;
    private final long jwtExpirationInMs;
    private final JwtParser jwtParser;

    /**
     * Constructs a JwtTokenProvider instance, initializing the secret key and token expiration duration.
     * The secret key is used to sign and validate JWT tokens, and the expiration duration defines
     * the validity period of the generated tokens in milliseconds.
     *
     * @param jwtSecret the secret key used for signing and validating JWT tokens
     * @param jwtExpirationInMs the expiration duration of the JWT tokens in milliseconds
     */
    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String jwtSecret,
            @Value("${security.jwt.token-expiration}") long jwtExpirationInMs
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationInMs = jwtExpirationInMs;
        SecretKey key = Keys.hmacShaKeyFor(this.jwtSecret.getBytes());
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }

    /**
     * Generates a JSON Web Token (JWT) for the specified user. The token includes
     * the user's email as the subject and their role as a claim, and it is signed
     * using a secret key to ensure integrity. The token also has an expiration time
     * based on the configured duration.
     *
     * @param user the user for whom the JWT is to be generated. The user's email and role
     *             will be embedded as claims in the token.
     * @return a compact, URL-safe JWT string containing the user's information and expiration details.
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extracts the username from the provided JWT token.
     * The username is retrieved from the "subject" claim within the token.
     *
     * @param token the JWT token from which the username is to be extracted
     * @return the username embedded in the token as the "subject" claim
     */
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Validates the provided JWT token by attempting to parse its claims. If the token
     * is successfully parsed without exceptions, it is considered valid. Otherwise, an
     * exception is caught, and the method returns false, indicating the token is invalid.
     *
     * @param token the JWT token to be validated
     * @return true if the token is valid and claims are successfully parsed, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extracts and returns the claims from the provided JWT token.
     * The claims represent the payload of the token, containing information
     * such as the subject, expiration, issuer, and any additional custom data.
     *
     * @param token the JWT token from which the claims are to be extracted
     * @return the Claims object containing the parsed payload of the token
     */
    private Claims getClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

}