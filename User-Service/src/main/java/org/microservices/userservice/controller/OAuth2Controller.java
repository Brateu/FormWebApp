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

@RestController
@RequestMapping("/api/v1/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final UserServiceImpl userService;

    @GetMapping("/google")
    public void initiateGoogleAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }


    @GetMapping("/success")
    public ResponseEntity<AuthResponseDto> handleOAuth2Success(@AuthenticationPrincipal OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");

        return ResponseEntity.ok(userService.authenticateOAuthUser(email, name, providerId));
    }
}