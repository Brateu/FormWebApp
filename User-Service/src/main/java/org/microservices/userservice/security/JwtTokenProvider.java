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
public class JwtTokenProvider {

    private Role role;
    private final String jwtSecret;
    private final long jwtExpirationInMs;
    private final JwtParser jwtParser;

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

    public Claims getClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    // Token validation is now handled by the API Gateway

    // These methods are kept for backward compatibility but are not used for validation
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

}