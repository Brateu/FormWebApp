package org.microservices.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Lightweight filter to enforce Authorization header presence for sensitive endpoints
 * while keeping the service in permitAll mode (gateway validates JWT). It performs a
 * quick parse of the token using JwtTokenProvider to ensure the token is structurally valid.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenRequiredFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private boolean isProtectedEndpoint(String path) {
        if (path == null) return false;
        return 
            path.equals("/api/user/details") || path.startsWith("/api/user/details") ||
            path.equals("/api/user/change-password") || path.startsWith("/api/user/change-password") ||
            path.equals("/api/user/deactivate-account") || path.startsWith("/api/user/deactivate-account");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (!isProtectedEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || auth.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = auth.trim();
        if (token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7).trim();
        }
        if (token.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String subject = jwtTokenProvider.getUsernameFromToken(token);
            if (subject == null || subject.isBlank()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (Exception ex) {
            log.debug("Token validation failed for {}: {}", path, ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
