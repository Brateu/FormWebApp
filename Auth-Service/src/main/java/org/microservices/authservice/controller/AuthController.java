package org.microservices.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.microservices.authservice.DTO.AuthResponseDto;
import org.microservices.authservice.DTO.LoginRequestDto;
import org.microservices.authservice.DTO.RegisterRequestDto;
import org.microservices.authservice.service.AuthServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.nio.file.attribute.UserPrincipal;

/**
 * The AuthController class is responsible for handling authentication-related API operations
 * such as user registration, login, and retrieving the current authenticated user's details.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    /**
     * Handles the registration of a new user.
     *
     * @param request the registration request containing the user's email, password, and full name
     * @return a ResponseEntity containing an AuthResponseDto with the authentication token for the registered user
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register (@RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }
    /**
     * Handles the login of a user.
     *
     * @param request the login request containing the user's email and password
     * @return a ResponseEntity containing an AuthResponseDto with the authentication token for the logged-in user
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login (@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }
    /**
     * Retrieves the details of the currently authenticated user.
     *
     * @param user the currently authenticated user's principal, provided by the security context
     * @return a ResponseEntity containing an AuthResponseDto with the authentication token for the authenticated user
     */
    @PostMapping("/me")
    public ResponseEntity<AuthResponseDto> getCurrentUser (@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(authService.getCurrentUser(user));
    }
}