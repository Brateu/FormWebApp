package org.microservices.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.microservices.userservice.DTO.AuthResponseDto;
import org.microservices.userservice.service.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Controller for handling OAuth2 authentication endpoints.
 * Manages OAuth2 authentication success and token handling.
 */
@RestController
@RequestMapping("/api/v1/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    /**
     * Service for user-related operations.
     */
    private final UserServiceImpl userService;

    /**
     * Handles the OAuth2 authentication success.
     * Extracts user information from the OAuth2User and authenticates the user.
     *
     * @param oauth2User the authenticated OAuth2 user
     * @return ResponseEntity containing authentication response with JWT token
     */
    @GetMapping("/success")
    public ResponseEntity<AuthResponseDto> handleOAuth2Success(@AuthenticationPrincipal OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");

        return ResponseEntity.ok(userService.authenticateOAuthUser(email, name, providerId));
    }

    /**
     * Handles the OAuth2 success redirect with token parameter.
     * Receives the JWT token as a query parameter and returns it to the client.
     *
     * @param token the JWT token
     * @return ResponseEntity containing the JWT token
     */
    @GetMapping("/success/token")
    public ResponseEntity<String> handleOAuth2SuccessWithToken(@RequestParam String token) {
        String jsonResponse = "{\"token\":\"" + token + "\",\"tokenType\":\"Bearer\"}";
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(jsonResponse);
    }
}