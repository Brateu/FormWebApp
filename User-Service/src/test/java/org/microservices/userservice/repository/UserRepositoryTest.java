package org.microservices.userservice.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.microservices.userservice.entity.User;
import org.microservices.userservice.enums.AuthProvider;
import org.microservices.userservice.enums.Role;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        User expectedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .fullName("Test User")
                .authProvider(AuthProvider.LOCAL)
                .role(Role.USER)
                .enabled(true)
                .build();
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(expectedUser));
        
        // Act
        Optional<User> result = userRepository.findByEmail("test@example.com");
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
    }
    
    @Test
    void findByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        // Act
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");
        
        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    void existsByEmail_ShouldReturnTrue_WhenUserExists() {
        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // Act
        boolean result = userRepository.existsByEmail("test@example.com");
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    void existsByEmail_ShouldReturnFalse_WhenUserDoesNotExist() {
        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);
        
        // Act
        boolean result = userRepository.existsByEmail("nonexistent@example.com");
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void save_ShouldReturnSavedUser() {
        // Arrange
        UserRepository userRepository = mock(UserRepository.class);
        User userToSave = User.builder()
                .email("new@example.com")
                .password("encodedPassword")
                .fullName("New User")
                .authProvider(AuthProvider.LOCAL)
                .role(Role.USER)
                .enabled(true)
                .build();
        
        User savedUser = User.builder()
                .id(1L)
                .email("new@example.com")
                .password("encodedPassword")
                .fullName("New User")
                .authProvider(AuthProvider.LOCAL)
                .role(Role.USER)
                .enabled(true)
                .build();
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // Act
        User result = userRepository.save(userToSave);
        
        // Assert
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getEmail(), result.getEmail());
        assertEquals(savedUser.getFullName(), result.getFullName());
    }
}