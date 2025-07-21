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
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

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

        // Extract provider-specific information
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

        // Process user authentication
        processOAuthUser(email, name, providerId, authProvider, response);
    }

    private void processOAuthUser(String email, String name, String providerId,
                                  AuthProvider authProvider, HttpServletResponse response) throws IOException {
        // Check if user exists
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // Create shadow account for OAuth user
            AuthResponseDto authResponse = userService.authenticateOAuthUser(email, name, providerId);
            sendAuthResponse(response, authResponse);
            return;
        }

        // User exists, check if provider is correct
        User user = userOptional.get();
        if (user.getAuthProvider() != authProvider) {
            log.error("User with email {} is already registered with provider {}",
                    email, user.getAuthProvider());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }

        // Generate token for existing user
        String token = jwtTokenProvider.generateToken(user);
        sendAuthResponse(response, new AuthResponseDto(token));
    }

    private void sendAuthResponse(HttpServletResponse response, AuthResponseDto authResponse)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(authResponse));
    }
}