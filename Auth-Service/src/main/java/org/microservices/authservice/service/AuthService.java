package org.microservices.authservice.service;

import org.microservices.authservice.DTO.AuthResponseDto;
import org.microservices.authservice.DTO.LoginRequestDto;
import org.microservices.authservice.DTO.RegisterRequestDto;
import org.microservices.authservice.exceptions.AuthServiceException;

import java.security.Principal;

/**
 * AuthService defines the contract for authentication-related operations, including
 * user registration, login, and retrieval of the current authenticated user's details.
 */
public interface AuthService {
    /**
     * Retrieves the details of the currently authenticated user.
     *
     * @param user the authenticated user's principal, provided by the security context
     * @return an AuthResponseDto containing the authentication token for the current user
     */
    public AuthResponseDto getCurrentUser(Principal user);
    /**
     * Registers a new user based on the provided registration request data.
     *
     * @param request an instance of {@link RegisterRequestDto} containing the user's registration information,
     *                including email, password, and full name.
     * @return an instance of {@link AuthResponseDto} containing the generated authentication token and token type
     *         for the registered user.
     * @throws AuthServiceException if the email provided in the request is already in use.
     */
    public AuthResponseDto register(RegisterRequestDto request);
    /**
     * Authenticates a user based on the provided login credentials and returns an
     * authentication response containing a JWT token.
     *
     * @param request the login request containing the user's email and password
     * @return an authentication response containing the generated JWT token
     * @throws AuthServiceException if the user is not found or credentials are invalid
     */
    public AuthResponseDto login(LoginRequestDto request);

}