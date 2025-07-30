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
/**
 * Custom implementation of OAuth2UserService for handling OAuth2 authentication.
 * This service extends the DefaultOAuth2UserService to provide custom processing
 * of OAuth2 user information, including user registration and updates.
 * 
 * It supports multiple OAuth2 providers (Google, GitHub) and handles the extraction
 * of user details from provider-specific attributes.
 */
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    /**
     * Repository for accessing and manipulating user data.
     */
    private final UserRepository userRepository;

    /**
     * Encoder for securely hashing passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Loads the user by OAuth2 user request.
     * Overrides the default implementation to provide custom processing of OAuth2 users.
     *
     * @param userRequest the user request
     * @return the OAuth2User
     * @throws OAuth2AuthenticationException if an authentication error occurs
     */
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

    /**
     * Processes the OAuth2 user information.
     * Extracts user details from the OAuth2 user attributes based on the provider,
     * and either registers a new user or updates an existing one.
     *
     * @param userRequest the OAuth2 user request
     * @param oauth2User the OAuth2 user
     * @return the processed OAuth2 user
     * @throws OAuth2AuthenticationException if an authentication error occurs
     */
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();

        AuthProvider authProvider = determineAuthProvider(registrationId);

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

        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isEmpty()) {
            user = registerNewUser(email, fullName, providerId, authProvider, Role.USER);
            log.info("New user registered via OAuth2: {}", email);
        } else {
            user = updateExistingUser(userOptional.get(), fullName, authProvider, providerId);
            log.info("Existing user updated via OAuth2: {}", email);
        }

        return new DefaultOAuth2User(
                Collections.emptyList(),
                attributes,
                nameAttributeKey
        );
    }

    /**
     * Determines the authentication provider based on the registration ID.
     *
     * @param registrationId the OAuth2 registration ID
     * @return the corresponding AuthProvider enum value
     * @throws OAuth2AuthenticationException if the provider is not supported
     */
    private AuthProvider determineAuthProvider(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> AuthProvider.GOOGLE;
            case "github" -> AuthProvider.GITHUB;
            default -> throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId);
        };
    }

    /**
     * Registers a new user with OAuth2 credentials.
     * Creates a new user entity with the provided details and a randomly generated password.
     *
     * @param email the user's email address
     * @param fullName the user's full name
     * @param providerId the provider-specific user ID
     * @param provider the authentication provider
     * @param role the user's role
     * @return the newly created user entity
     */
    private User registerNewUser(String email, String fullName, String providerId, AuthProvider provider, Role role) {
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

    /**
     * Updates an existing user with OAuth2 credentials.
     * Verifies that the authentication provider matches and updates the user's details.
     *
     * @param existingUser the existing user entity
     * @param fullName the user's full name (may be updated)
     * @param provider the authentication provider
     * @param providerId the provider-specific user ID
     * @return the updated user entity
     * @throws OAuth2AuthenticationException if the provider doesn't match the existing user's provider
     */
    private User updateExistingUser(User existingUser, String fullName, AuthProvider provider, String providerId) {
        if (existingUser.getAuthProvider() != provider) {
            String errorMessage = "User with email " + existingUser.getEmail() +
                    " is already registered with provider " + existingUser.getAuthProvider();
            log.error(errorMessage);
            throw new OAuth2AuthenticationException(errorMessage);
        }

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