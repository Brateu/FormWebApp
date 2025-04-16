package org.microservices.authservice.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.microservices.authservice.service.CustomerUserDetailService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtTokenFilter is responsible for filtering HTTP requests to validate and process
 * JWT Bearer tokens. This class extends OncePerRequestFilter, ensuring the logic
 * is executed once per request within a single servlet request processing cycle.
 *
 * The filter extracts the JWT token from the Authorization header of the incoming request,
 * validates the token, retrieves the user details associated with it, and sets
 * the authentication context in the SecurityContextHolder.
 *
 * If the JWT token is expired, malformed, unsupported, or invalid, the filter
 * sends an appropriate HTTP error response such as SC_UNAUTHORIZED or SC_INTERNAL_SERVER_ERROR.
 *
 * Key responsibilities:
 * - Extract JWT token from the Authorization header
 * - Validate the extracted JWT token using JwtTokenProvider
 * - Load user details using CustomerUserDetailService
 * - Create and set the authentication in the SecurityContextHolder
 * - Log information such as user authentication and token-related issues
 *
 * Note: This filter operates in the Spring Security filter chain and is designed
 * to handle JWT-based authentication. The filter does not execute when the SecurityContextHolder
 * already has a valid authentication object.
 */
@Service
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomerUserDetailService userDetailsService;

    /**
     * Constructs a new JwtTokenFilter instance.
     *
     * @param jwtTokenProvider the JwtTokenProvider responsible for JWT token operations such as validation and extracting user details
     * @param userDetailsService the CustomerUserDetailService to load user details based on the extracted username from the JWT token
     */
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, CustomerUserDetailService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Processes each HTTP request to validate and handle JWT tokens. This method extracts
     * the JWT token from the authorization header, validates it, retrieves the associated
     * user details, and sets up the authentication context in the SecurityContextHolder.
     * It also handles token-related exceptions by sending the appropriate error responses.
     *
     * @param request the HttpServletRequest containing client request data
     * @param response the HttpServletResponse for sending responses to the client
     * @param filterChain the FilterChain to proceed with the next filter in the chain
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String email = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                email = jwtTokenProvider.getUsernameFromToken(token);
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtTokenProvider.validateToken(token)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("Successfully authenticated user: {}", userDetails.getUsername());
                }
            }
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage(), ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT token");
            return;
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage(), ex);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        } catch (Exception ex) {
            log.error("Could not authenticate user: {}", ex.getMessage(), ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication failed");
            return;

        }

        filterChain.doFilter(request, response);
    }
}