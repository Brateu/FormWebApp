package org.microservices.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.microservices.userservice.DTO.*;
import org.microservices.userservice.entity.User;
import org.microservices.userservice.exceptions.UserNotFoundException;
import org.microservices.userservice.repository.UserRepository;
import org.microservices.userservice.service.UserServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for handling user-related HTTP requests.
 * Provides endpoints for user registration, login, logout, and retrieving user details.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    /**
     * Service for user-related operations.
     */
    private final UserServiceImpl userService;

    /**
     * Repository for accessing user data.
     */
    private final UserRepository userRepository;

    /**
     * Registers a new user in the system.
     *
     * @param request the registration request details
     * @return ResponseEntity containing authentication response with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register (@jakarta.validation.Valid @RequestBody RegisterRequestDto request) {
        return ResponseEntity.ok(userService.register(request));
    }

    /**
     * Authenticates a user and generates a JWT token.
     * If provider parameter is specified, redirects to OAuth2 authentication.
     *
     * @param request the login request details
     * @param provider the OAuth2 provider (google or github)
     * @param response the HTTP response
     * @return ResponseEntity containing authentication response with JWT token
     * @throws IOException if an I/O error occurs during redirect
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @RequestBody(required = false) LoginRequestDto request,
            @RequestParam(required = false) String provider,
            HttpServletResponse response) throws IOException {

        if (provider != null && !provider.isEmpty()) {
            if ("google".equalsIgnoreCase(provider)) {
                response.sendRedirect("/oauth2/authorization/google");
                return null;
            } else if ("github".equalsIgnoreCase(provider)) {
                response.sendRedirect("/oauth2/authorization/github");
                return null;
            } else {
                return ResponseEntity.badRequest().body(new AuthResponseDto("Invalid provider: " + provider, true));
            }
        }

        if (request == null) {
            return ResponseEntity.badRequest().body(new AuthResponseDto("Login request cannot be null", true));
        }

        return ResponseEntity.ok(userService.login(request));
    }



    /**
     * Retrieves the details of the currently authenticated user.
     *
     * @param authentication the authentication object from the security context
     * @return ResponseEntity containing the user details
     */
    @GetMapping("/details")
    public ResponseEntity<UserDto> getCurrentUser(
            Authentication authentication,
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader
    ) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication, authorizationHeader));
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

    /**
     * Retrieves a user's ID by their email address using request parameter.
     *
     * @param email the email address of the user to look up
     * @return ResponseEntity containing the user ID if found, or 404 if not found
     */
    @GetMapping("/id-by-email")
    public ResponseEntity<Long> getUserIdByEmailParam(@RequestParam String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get().getId());
        }
        throw UserNotFoundException.withEmail(email);
    }

    /**
     * Searches for users by a query string and returns their IDs.
     * The query can match against email, fullName, or any other relevant field.
     *
     * @param query the search query
     * @return ResponseEntity containing an array of user IDs that match the query
     */
    @GetMapping("/search")
    public ResponseEntity<Long[]> searchUsers(@RequestParam String query) {
        if (query == null || query.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<User> users = userRepository.findAll().stream()
                .filter(user -> 
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(query.toLowerCase())) ||
                    (user.getFullName() != null && user.getFullName().toLowerCase().contains(query.toLowerCase())))
                .toList();

        Long[] userIds = users.stream()
                .map(User::getId)
                .toArray(Long[]::new);

        return ResponseEntity.ok(userIds);
    }

    /**
     * Retrieves a user's ID by their email address using path variable.
     * This endpoint is designed to be compatible with Feign clients.
     *
     * @param email the email address of the user to look up
     * @return The user ID if found, or null if not found
     */
    @GetMapping(value = "/email/{email}/id", produces = "application/json")
    public ResponseEntity<Long> getUserIdByEmail(@PathVariable String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userOptional.get().getId());
        }
        throw UserNotFoundException.withEmail(email);
    }
    /**
     * Handles the password change request for a user. This method verifies the given token
     * for authentication and processes the password change based on the provided request details.
     *
     * @param token the authorization token from the request header, used to authenticate the user
     * @param request an object containing the old password and new password details for the password change
     * @return a {@code ResponseEntity} containing a message indicating the success or failure of the password change
     */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String token,
            @jakarta.validation.Valid @RequestBody ChangePasswordRequestDto request
    ) {
        String message = userService.changePassword(token, request);
        return ResponseEntity.ok(message);
    }

    /**
     * Deactivates a user account based on the provided authorization token.
     * This method invokes the user service to perform the deactivation logic
     * and returns a success message if the operation is completed.
     *
     * @param token the authorization token provided in the request header; used to identify and authenticate the user
     * @return a {@code ResponseEntity} containing a success message as {@code String} upon successful deactivation
     */
    @PostMapping("/deactivate-account")
    public ResponseEntity<String> deactivateAccount(@RequestHeader("Authorization") String token){
        String message = userService.deactivateAccount(token);
        return ResponseEntity.ok(message);
    }
}