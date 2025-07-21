package org.microservices.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.microservices.userservice.DTO.AuthResponseDto;
import org.microservices.userservice.DTO.LoginRequestDto;
import org.microservices.userservice.DTO.RegisterRequestDto;
import org.microservices.userservice.DTO.UserDto;
import org.microservices.userservice.service.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling user-related HTTP requests.
 * Provides endpoints for user registration, login, logout, and retrieving user details.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    /**
     * Registers a new user in the system.
     *
     * @param request the registration request details
     * @return ResponseEntity containing authentication response with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register (@RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(userService.register(request));
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request the login request details
     * @return ResponseEntity containing authentication response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login (@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(userService.login(request));
    }

    /**
     * Retrieves the details of the currently authenticated user.
     *
     * @param authentication the authentication object from the security context
     * @return ResponseEntity containing the user details
     */
    @GetMapping("/details")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication));
    }

    /**
     * Logs out the currently authenticated user.
     * This invalidates the user's session and any associated tokens.
     *
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        boolean success = userService.logout();
        if (success) {
            return ResponseEntity.ok("Logged out successfully");
        } else {
            return ResponseEntity.internalServerError().body("Error during logout");
        }
    }
}