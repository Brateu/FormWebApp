package org.microservices.userservice.security.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microservices.userservice.entity.User;
import org.microservices.userservice.enums.AuthProvider;
import org.microservices.userservice.enums.Role;
import org.microservices.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user: {}", ex.getMessage(), ex);

            OAuth2Error error = new OAuth2Error("invalid_token", ex.getMessage(), null);
            throw new OAuth2AuthenticationException(error, ex);
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();

        // Determine the authentication provider
        AuthProvider authProvider = determineAuthProvider(registrationId);

        // Extract the necessary user information based on the provider
        String email;
        String fullName;
        String providerId;
        String nameAttributeKey;

        switch (authProvider) {
            case GOOGLE:
                email = (String) attributes.get("email");
                fullName = (String) attributes.get("name");
                providerId = (String) attributes.get("sub");
                nameAttributeKey = "sub";
                break;

            case GITHUB:
                email = (String) attributes.get("email");
                if (email == null) {
                    String login = (String) attributes.get("login");
                    email = login + "@github.com";
                }

                fullName = (String) attributes.get("name");
                if (fullName == null) {
                    fullName = (String) attributes.get("login");
                }

                providerId = String.valueOf(attributes.get("id"));
                nameAttributeKey = "id";
                break;

            default:
                throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId);
        }

        // Check if the user already exists
        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isEmpty()) {
            // Register a new user
            user = registerNewUser(email, fullName, providerId, authProvider, Role.USER);
            log.info("New user registered via OAuth2: {}", email);
        } else {
            // Update existing user
            user = updateExistingUser(userOptional.get(), fullName, authProvider, providerId);
            log.info("Existing user updated via OAuth2: {}", email);
        }

        // Create new OAuth2User with updated attributes
        return new DefaultOAuth2User(
                Collections.emptyList(),
                attributes,
                nameAttributeKey
        );
    }

    private AuthProvider determineAuthProvider(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> AuthProvider.GOOGLE;
            case "github" -> AuthProvider.GITHUB;
            default -> throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId);
        };
    }

    private User registerNewUser(String email, String fullName, String providerId, AuthProvider provider, Role role) {
        // Generate a random password for OAuth users
        String randomPassword = UUID.randomUUID().toString();

        User user = User.builder()
                .email(email)
                .fullName(fullName)
                .providerId(providerId)
                .authProvider(provider)
                .role(role)
                .password(passwordEncoder.encode(randomPassword))
                .build();

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, String fullName, AuthProvider provider, String providerId) {
        // Verify the provider matches
        if (existingUser.getAuthProvider() != provider) {
            String errorMessage = "User with email " + existingUser.getEmail() +
                    " is already registered with provider " + existingUser.getAuthProvider();
            log.error(errorMessage);
            throw new OAuth2AuthenticationException(errorMessage);
        }

        // Update user information using builder pattern
        User updatedUser = User.builder()
                .id(existingUser.getId())
                .email(existingUser.getEmail())
                .password(existingUser.getPassword())
                .fullName(fullName)
                .authProvider(existingUser.getAuthProvider())
                .providerId(providerId)
                .role(existingUser.getRole())
                .enabled(existingUser.isEnabled())
                .build();

        return userRepository.save(updatedUser);
    }
}