package org.microservices.userservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.microservices.userservice.DTO.AuthResponseDto;
import org.microservices.userservice.service.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2ControllerTest {

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private OAuth2Controller oAuth2Controller;

    private final String TEST_TOKEN = "test-jwt-token";

    @BeforeEach
    void setUp() {
        // No setup needed for the token endpoint test
    }

    @Test
    void handleOAuth2SuccessWithToken_ShouldReturnOkResponse_WithTokenJson() {
        // Act
        ResponseEntity<String> response = oAuth2Controller.handleOAuth2SuccessWithToken(TEST_TOKEN);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("application/json", response.getHeaders().getFirst("Content-Type"));
        
        // Check that the response body contains the token and tokenType
        String expectedJson = "{\"token\":\"" + TEST_TOKEN + "\",\"tokenType\":\"Bearer\"}";
        assertEquals(expectedJson, response.getBody());
    }

    @Test
    void handleOAuth2Success_ShouldReturnOkResponse_WithAuthResponseDto() {
        // Arrange
        OAuth2User oauth2User = mock(OAuth2User.class);
        when(oauth2User.getAttribute("email")).thenReturn("test@example.com");
        when(oauth2User.getAttribute("name")).thenReturn("Test User");
        when(oauth2User.getAttribute("sub")).thenReturn("12345");
        
        AuthResponseDto authResponseDto = new AuthResponseDto(TEST_TOKEN);
        when(userService.authenticateOAuthUser(anyString(), anyString(), anyString())).thenReturn(authResponseDto);

        // Act
        ResponseEntity<AuthResponseDto> response = oAuth2Controller.handleOAuth2Success(oauth2User);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponseDto, response.getBody());
    }
}