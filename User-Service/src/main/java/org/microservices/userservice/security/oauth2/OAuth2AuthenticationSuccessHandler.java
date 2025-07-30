package org.microservices.userservice.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microservices.userservice.DTO.AuthResponseDto;
import org.microservices.userservice.entity.User;
import org.microservices.userservice.enums.AuthProvider;
import org.microservices.userservice.repository.UserRepository;
import org.microservices.userservice.security.JwtTokenProvider;
import org.microservices.userservice.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
/**
 * Handler for successful OAuth2 authentication.
 * This class extends SimpleUrlAuthenticationSuccessHandler to provide custom handling
 * of successful OAuth2 authentication events. It extracts user information from
 * the OAuth2 authentication token, processes the user, and generates a JWT token
 * for subsequent API calls.
 */
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    /**
     * Service for user-related operations.
     */
    private final UserService userService;

    /**
     * Provider for JWT token generation and validation.
     */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Repository for accessing and manipulating user data.
     */
    private final UserRepository userRepository;

    /**
     * JSON object mapper for serialization and deserialization.
     */
    private final ObjectMapper objectMapper;

    /**
     * Handles successful OAuth2 authentication.
     * This method is called when a user is successfully authenticated via OAuth2.
     * It extracts user information from the authentication token and processes the user.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param authentication the authentication object
     * @throws IOException if an I/O error occurs during redirection
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            log.error("Authentication is not an OAuth2AuthenticationToken: {}",
                    authentication.getClass().getName());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email;
        String name;
        String providerId;
        AuthProvider authProvider;

        switch (registrationId) {
            case "github":
                email = (String) attributes.get("email");
                if (email == null) {
                    String login = (String) attributes.get("login");
                    email = login + "@github.com";
                }
                name = (String) attributes.get("name");
                if (name == null) {
                    name = (String) attributes.get("login");
                }
                providerId = String.valueOf(attributes.get("id"));
                authProvider = AuthProvider.GITHUB;
                break;

            case "google":
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                providerId = (String) attributes.get("sub");
                authProvider = AuthProvider.GOOGLE;
                break;

            default:
                log.error("Unsupported OAuth2 provider: {}", registrationId);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
        }

        processOAuthUser(email, name, providerId, authProvider, response);
    }

    /**
     * Processes an OAuth2 user after successful authentication.
     * Checks if the user exists in the database and either creates a new user
     * or authenticates an existing one. Generates a JWT token for the user.
     *
     * @param email the user's email address
     * @param name the user's full name
     * @param providerId the provider-specific user ID
     * @param authProvider the authentication provider
     * @param response the HTTP response
     * @throws IOException if an I/O error occurs during redirection
     */
    private void processOAuthUser(String email, String name, String providerId,
                                  AuthProvider authProvider, HttpServletResponse response) throws IOException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            AuthResponseDto authResponse = userService.authenticateOAuthUser(email, name, providerId);
            sendAuthResponse(response, authResponse);
            return;
        }

        User user = userOptional.get();
        if (user.getAuthProvider() != authProvider) {
            log.error("User with email {} is already registered with provider {}",
                    email, user.getAuthProvider());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }

        String token = jwtTokenProvider.generateToken(user);
        sendAuthResponse(response, new AuthResponseDto(token));
    }

    /**
     * Sends the authentication response to the client.
     * Redirects the client to a success endpoint with the JWT token as a query parameter.
     *
     * @param response the HTTP response
     * @param authResponse the authentication response containing the JWT token
     * @throws IOException if an I/O error occurs during redirection
     */
    private void sendAuthResponse(HttpServletResponse response, AuthResponseDto authResponse)
            throws IOException {
        String redirectUrl = "/api/v1/auth/oauth2/success/token?token=" + authResponse.getToken();
        response.sendRedirect(redirectUrl);
    }
}