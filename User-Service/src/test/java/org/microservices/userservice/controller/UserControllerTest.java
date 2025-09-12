package org.microservices.userservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.microservices.userservice.DTO.AuthResponseDto;
import org.microservices.userservice.DTO.LoginRequestDto;
import org.microservices.userservice.DTO.RegisterRequestDto;
import org.microservices.userservice.DTO.UserDto;
import org.microservices.userservice.enums.AuthProvider;
import org.microservices.userservice.service.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private Authentication authentication;

    @Mock
    private jakarta.servlet.http.HttpServletResponse httpServletResponse;

    @InjectMocks
    private UserController userController;

    private RegisterRequestDto registerRequestDto;
    private LoginRequestDto loginRequestDto;
    private UserDto userDto;
    private AuthResponseDto authResponseDto;
    private final String TEST_TOKEN = "test-jwt-token";

    @BeforeEach
    void setUp() {
        // Setup register request
        registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setEmail("test@example.com");
        registerRequestDto.setPassword("password");
        registerRequestDto.setFullName("Test User");

        // Setup login request
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test@example.com");
        loginRequestDto.setPassword("password");

        // Setup user DTO
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setFullName("Test User");
        userDto.setAuthProvider(AuthProvider.LOCAL);

        // Setup auth response
        authResponseDto = new AuthResponseDto(TEST_TOKEN);
    }

    @Test
    void register_ShouldReturnOkResponse_WithAuthResponseDto() {
        // Arrange
        when(userService.register(any(RegisterRequestDto.class))).thenReturn(authResponseDto);

        // Act
        ResponseEntity<AuthResponseDto> response = userController.register(registerRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponseDto, response.getBody());
        verify(userService).register(registerRequestDto);
    }

    @Test
    void login_ShouldReturnOkResponse_WithAuthResponseDto() throws IOException {
        // Arrange
        when(userService.login(any(LoginRequestDto.class))).thenReturn(authResponseDto);

        // Act
        ResponseEntity<AuthResponseDto> response = userController.login(loginRequestDto, null, httpServletResponse);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponseDto, response.getBody());
        verify(userService).login(loginRequestDto);
    }

    @Test
    void getCurrentUser_ShouldReturnOkResponse_WithUserDto() {
        // Arrange
        when(userService.getCurrentUser(any(Authentication.class), any())).thenReturn(userDto);

        // Act
        ResponseEntity<UserDto> response = userController.getCurrentUser(authentication, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
        verify(userService).getCurrentUser(authentication, null);
    }

    @Test
    void logout_ShouldReturnOkResponse_WhenLogoutSuccessful() {
        // Arrange
        when(userService.logout()).thenReturn(true);

        // Act
        ResponseEntity<String> response = userController.logout();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out successfully", response.getBody());
        verify(userService).logout();
    }

    @Test
    void logout_ShouldReturnInternalServerError_WhenLogoutFails() {
        // Arrange
        when(userService.logout()).thenReturn(false);

        // Act
        ResponseEntity<String> response = userController.logout();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error during logout", response.getBody());
        verify(userService).logout();
    }
}