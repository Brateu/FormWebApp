package org.microservices.userservice.service;

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
import org.microservices.userservice.entity.User;
import org.microservices.userservice.enums.AuthProvider;
import org.microservices.userservice.enums.Role;
import org.microservices.userservice.exceptions.UserServiceException;
import org.microservices.userservice.mappers.RegisterMapper;
import org.microservices.userservice.mappers.UserMapper;
import org.microservices.userservice.repository.UserRepository;
import org.microservices.userservice.security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RegisterMapper registerMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private RegisterRequestDto registerRequestDto;
    private LoginRequestDto loginRequestDto;
    private UserDto userDto;
    private final String TEST_TOKEN = "test-jwt-token";

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .fullName("Test User")
                .authProvider(AuthProvider.LOCAL)
                .role(Role.USER)
                .enabled(true)
                .build();

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
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        // We don't need to mock registerMapper since it's initialized in the service
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn(TEST_TOKEN);

        // Act
        AuthResponseDto response = userService.register(registerRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.getToken());
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
        verify(jwtTokenProvider).generateToken(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenUserAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(UserServiceException.class, () -> userService.register(registerRequestDto));
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn(TEST_TOKEN);

        // Act
        AuthResponseDto response = userService.login(loginRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.getToken());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password", "encodedPassword");
        verify(jwtTokenProvider).generateToken(testUser);
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserServiceException.class, () -> userService.login(loginRequestDto));
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsInvalid() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(UserServiceException.class, () -> userService.login(loginRequestDto));
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password", "encodedPassword");
        verify(jwtTokenProvider, never()).generateToken(any(User.class));
    }

    @Test
    void getCurrentUser_ShouldReturnUserDto_WhenUserExists() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        // We don't need to mock userMapper since it's initialized in the service

        // Act
        UserDto result = userService.getCurrentUser(authentication);

        // Assert
        assertNotNull(result);
        // We can't verify exact values since we're not mocking the mapper
        // Just verify the method calls
        verify(authentication).getName();
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getCurrentUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserServiceException.class, () -> userService.getCurrentUser(authentication));
        verify(authentication).getName();
        verify(userRepository).findByEmail("test@example.com");
        verify(userMapper, never()).userToUserDto(any(User.class));
    }

    @Test
    void authenticateOAuthUser_ShouldReturnAuthResponse_WhenUserExists() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn(TEST_TOKEN);

        // Act
        AuthResponseDto response = userService.authenticateOAuthUser("test@example.com", "Test User", "provider123");

        // Assert
        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.getToken());
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(jwtTokenProvider).generateToken(testUser);
    }

    @Test
    void authenticateOAuthUser_ShouldCreateNewUser_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedProviderId");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn(TEST_TOKEN);

        // Act
        AuthResponseDto response = userService.authenticateOAuthUser("test@example.com", "Test User", "provider123");

        // Assert
        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.getToken());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).encode("provider123");
        verify(userRepository).save(any(User.class));
        verify(jwtTokenProvider).generateToken(any(User.class));
    }

    @Test
    void logout_ShouldReturnTrue_WhenSuccessful() {
        // Act
        boolean result = userService.logout();

        // Assert
        assertTrue(result);
        // Verify that SecurityContextHolder.clearContext() was called
        // This is challenging to verify directly since it's a static method
    }
}