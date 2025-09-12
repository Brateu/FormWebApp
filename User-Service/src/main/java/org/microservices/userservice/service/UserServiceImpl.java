package org.microservices.userservice.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.microservices.userservice.DTO.*;
import org.microservices.userservice.entity.User;
import org.microservices.userservice.enums.AuthProvider;
import org.microservices.userservice.enums.Role;
import org.microservices.userservice.exceptions.UserServiceException;
import org.microservices.userservice.mappers.RegisterMapper;
import org.microservices.userservice.mappers.UserMapper;
import org.microservices.userservice.repository.UserRepository;
import org.microservices.userservice.security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the {@link UserService} interface responsible for managing user authentication-related operations.
 * This service provides functionalities for user registration, login, and logout.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final RegisterMapper registerMapper = Mappers.getMapper(RegisterMapper.class);
  private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

  /**
   * Resolves the email of the caller from the provided Authorization token (Bearer ...) or the SecurityContext.
   * @param authorizationHeader value of Authorization header (can be null, with or without Bearer prefix)
   * @return email (subject) or null if cannot be resolved
   */
  private String resolveEmailFromTokenOrContext(String authorizationHeader) {
    // 1) Try Authorization header first
    try {
      if (authorizationHeader != null && !authorizationHeader.isBlank()) {
        String token = authorizationHeader.trim();
        if (token.toLowerCase().startsWith("bearer ")) {
          token = token.substring(7).trim();
        }
        if (!token.isBlank()) {
          return jwtTokenProvider.getUsernameFromToken(token);
        }
      }
    } catch (Exception ignored) {
      // fall back to context
    }

    // 2) Fallback to SecurityContext
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      return authentication.getName();
    }
    return null;
  }

  /**
   * Normalizes an email address by:
   * 1. Converting to lowercase for case-insensitivity
   * 2. Removing the plus alias part if present (e.g., user+alias@example.com -> user@example.com)
   *
   * @param email the email address to normalize
   * @return the normalized email address
   */
  private String normalizeEmail(String email) {
    if (email == null || email.isEmpty()) {
      return email;
    }

    // Convert to lowercase for case-insensitivity
    email = email.toLowerCase();

    // Handle plus alias
    int atIndex = email.indexOf('@');
    if (atIndex > 0) {
      int plusIndex = email.substring(0, atIndex).indexOf('+');
      if (plusIndex > 0) {
        email = email.substring(0, plusIndex) + email.substring(atIndex);
      }
    }

    return email;
  }

  /**
   * Registers a new user in the system. If a user with the provided email already exists, an exception will be thrown.
   * The user's password will be encoded, and a JWT token will be generated upon successful registration.
   *
   * @param request the DTO containing the user's registration details, including email, password, and other user information.
   * @return an {@link AuthResponseDto} containing the generated JWT token for the newly registered user.
   * @throws UserServiceException if a user with the provided email already exists.
   */
  @Override
  public AuthResponseDto register(RegisterRequestDto request) {
    if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
      throw new UserServiceException("Email cannot be blank");
    }

    // Normalize the email (handle case-insensitivity and plus aliases)
    String normalizedEmail = normalizeEmail(request.getEmail());

    if (userRepository.existsByEmail(normalizedEmail)) {
      throw new UserServiceException("Email already in use");
    }
    System.out.println("Request DTO fields: " + request.toString());

    User mappedUser = registerMapper.registerRequestDtoToUser(request);

    System.out.println("User object after mapping: " + mappedUser);

    User user = User.builder()
            .id(mappedUser.getId())
            .email(normalizedEmail) // Use normalized email
            .fullName(mappedUser.getFullName())
            .password(passwordEncoder.encode(request.getPassword()))
            .authProvider(AuthProvider.LOCAL)
            .role(Role.USER)
            .enabled(true)
            .build();

    System.out.println("User object before saving: " + user);

    user = userRepository.save(user);
    String token = jwtTokenProvider.generateToken(user);

    return AuthResponseDto.builder()
            .token(token)
            .tokenType("Bearer")
            .userId(user.getId())
            .build();
  }

  /**
   * Authenticates a user based on the provided login request data and generates a JWT token if the credentials are valid.
   *
   * @param request the login request data containing user email and password
   * @return an AuthResponseDto containing the generated JWT token for the authenticated user
   * @throws UserServiceException if the user is not found or the credentials are invalid
   */

  @Override
  public AuthResponseDto login(LoginRequestDto request) {
    if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
      throw new UserServiceException("Email cannot be blank");
    }

    // Normalize the email (handle case-insensitivity and plus aliases)
    String normalizedEmail = normalizeEmail(request.getEmail());

    User user =
        userRepository
            .findByEmail(normalizedEmail)
            .orElseThrow(() -> new UserServiceException("User not found"));

    if(!user.isEnabled()) {
        throw new UserServiceException("User account is disabled");
    }

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new UserServiceException("Invalid credentials");
    }

    String token = jwtTokenProvider.generateToken(user);

    return AuthResponseDto.builder()
            .token(token)
            .tokenType("Bearer")
            .userId(user.getId())
            .build();
  }

  /**
   * Retrieves the currently authenticated user from the security context.
   *
   * @param authentication the authentication object from the security context
   * @return the currently authenticated user's details
   * @throws UserServiceException if no authenticated user is found
   */
  @Override
  public UserDto getCurrentUser(Authentication authentication, String authorizationHeader) {
    String email = resolveEmailFromTokenOrContext(authorizationHeader);
    if ((email == null || email.isBlank()) && authentication != null) {
      try {
        email = authentication.getName();
      } catch (Exception ignored) {
      }
    }
    if (email == null || email.isBlank()) {
      throw new UserServiceException("No authenticated user context");
    }
    // Normalize the email (handle case-insensitivity and plus aliases)
    String normalizedEmail = normalizeEmail(email);

    User user = userRepository
            .findByEmail(normalizedEmail)
            .orElseThrow(() -> new UserServiceException("User not found"));

    return userMapper.userToUserDto(user);
  }

  /**
   * Authenticates a user using OAuth2 provider information.
   *
   * @param email the user's email address
   * @param name the user's full name
   * @param providerId the provider-specific user ID
   * @return authentication response containing a JWT token
   */
  @Override
  public AuthResponseDto authenticateOAuthUser(String email, String name, String providerId) {
    // Normalize the email (handle case-insensitivity and plus aliases)
    String normalizedEmail = normalizeEmail(email);

    User user = userRepository.findByEmail(normalizedEmail)
            .orElseGet(() -> {
              User newUser = User.builder()
                      .email(normalizedEmail) // Use normalized email
                      .fullName(name)
                      .password(passwordEncoder.encode(providerId))
                      .authProvider(AuthProvider.GITHUB)
                      .providerId(providerId)
                      .role(Role.USER)
                      .enabled(true)
                      .build();

              return userRepository.save(newUser);
            });


    String token = jwtTokenProvider.generateToken(user);

    return AuthResponseDto.builder()
            .token(token)
            .tokenType("Bearer")
            .userId(user.getId())
            .build();
  }

  /**
   * Logs out the currently authenticated user.
   * This invalidates the user's session and any associated tokens.
   *
   * @return true if logout was successful, false otherwise
   */
  @Override
  public boolean logout() {
    try {
      // Get the current user ID from the authentication context
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.isAuthenticated()) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        // User found, no action needed for logout
      }

      SecurityContextHolder.clearContext();
      log.info("User logged out successfully");
      return true;
    } catch (Exception e) {
      log.error("Error during logout: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Changes the password for a user identified by a token or user context.
   *
   * @param token the access token or identifier to resolve the user
   * @param request the DTO containing the current password, new password, and confirmation of the new password
   * @return a message indicating the result of the password change operation
   */
  @Override
    public String changePassword(String token, ChangePasswordRequestDto request) {
        try {
            if (request == null) {
                return "Request body cannot be null";
            }
            String currentPassword = request.getCurrentPassword();
            String newPassword = request.getNewPassword();
            String confirmNewPassword = request.getConfirmNewPassword();

            if (newPassword == null || newPassword.trim().isEmpty()) {
                return "New password cannot be empty";
            }
            if (newPassword.length() < 8) {
                return "New password must be at least 8 characters long";
            }
            if (confirmNewPassword == null || !newPassword.equals(confirmNewPassword)) {
                return "New password and confirmation do not match";
            }

            String email = resolveEmailFromTokenOrContext(token);
            if (email == null || email.isBlank()) {
                return "Invalid or missing token/user context";
            }
            String normalizedEmail = normalizeEmail(email);

            Optional<User> optionalUser = userRepository.findByEmail(normalizedEmail);
            if (optionalUser.isEmpty()) {
                return "User not found";
            }
            User user = optionalUser.get();

            if (currentPassword == null || currentPassword.isBlank()) {
                return "Current password is required";
            }
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return "Current password is incorrect";
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return "Password changed successfully";
        } catch (Exception e) {
            return "Error during password change: " + e.getMessage();
        }
  }

  /**
   * Deactivates the account associated with the given token. This method resolves the user based
   * on the provided token or context, disables the account if it is currently active, and clears
   * the security context. If the account is already deactivated or cannot be found, an appropriate
   * message will be returned.
   *
   * @param token the authentication token used to identify the user account to deactivate
   * @return a message indicating the outcome of the deactivation process, such as success,
   *         account not found, already deactivated, or error details
   */
  @Override
    public String deactivateAccount(String token) {
        try {
            String email = resolveEmailFromTokenOrContext(token);
            if (email == null || email.isBlank()) {
                return "Invalid or missing token/user context";
            }

            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                return "User not found";
            }
            User user = optionalUser.get();

            if (!user.isEnabled()) {
                // Already deactivated
                SecurityContextHolder.clearContext();
                return "Account already deactivated";
            }

            user.setEnabled(false);
            userRepository.save(user);
            SecurityContextHolder.clearContext();
            return "Account deactivated successfully";
        } catch (Exception e) {
            return "Error during account deactivation: " + e.getMessage();
        }
  }
}