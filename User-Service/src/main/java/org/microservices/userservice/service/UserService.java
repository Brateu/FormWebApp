package org.microservices.userservice.service;

import org.microservices.userservice.DTO.AuthResponseDto;
import org.microservices.userservice.DTO.ChangePasswordRequestDto;
import org.microservices.userservice.DTO.LoginRequestDto;
import org.microservices.userservice.DTO.RegisterRequestDto;
import org.microservices.userservice.DTO.UserDto;
import org.springframework.security.core.Authentication;

/**
 * Interface defining the contract for authentication service operations.
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     *
     * @param request the registration request details
     * @return authentication response containing a JWT token
     */
    AuthResponseDto register(RegisterRequestDto request);

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request the login request details
     * @return authentication response containing a JWT token
     */
    AuthResponseDto login(LoginRequestDto request);

    /**
     * Retrieves the currently authenticated user from the request context.
     * Prefer the Authorization header token (parsed locally) and fall back to the SecurityContext if available.
     *
     * @param authentication the authentication object from the security context (may be null)
     * @param authorizationHeader the Authorization header value (e.g., "Bearer <token>") (may be null)
     * @return the currently authenticated user's details
     */
    UserDto getCurrentUser(Authentication authentication, String authorizationHeader);

    /**
     * Authenticates a user using OAuth2 provider information.
     *
     * @param email the user's email address
     * @param name the user's full name
     * @param providerId the provider-specific user ID
     * @return authentication response containing a JWT token
     */
    AuthResponseDto authenticateOAuthUser(String email, String name, String providerId);

    /**
     * Logs out the currently authenticated user.
     * This invalidates the user's session and any associated tokens.
     *
     * @return true if logout was successful, false otherwise
     */
    boolean logout();

    /**
     * Changes the password for the currently authenticated user.
     * Validates the current password and ensures the new password meets security requirements.
     *
     * @param token the JWT token of the currently authenticated user
     * @param request the request payload containing the current password, new password, and confirmation of the new password
     * @return a string indicating the outcome of the password change operation (e.g., success message or error details)
     */
    String changePassword(String token, ChangePasswordRequestDto request);

    /**
     * Deactivates the account associated with the provided JWT token.
     * This operation disables the user's account, preventing further access to the system.
     *
     * @param token the JWT token of the currently authenticated user
     * @return a string indicating the outcome of the account deactivation operation (e.g., success message or error details)
     */
    String deactivateAccount(String token);
}