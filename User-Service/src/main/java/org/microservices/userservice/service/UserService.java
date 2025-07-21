package org.microservices.userservice.service;

import org.microservices.userservice.DTO.AuthResponseDto;
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
     * Retrieves the currently authenticated user from the security context.
     *
     * @param authentication the authentication object from the security context
     * @return the currently authenticated user's details
     */
    UserDto getCurrentUser(Authentication authentication);

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
}