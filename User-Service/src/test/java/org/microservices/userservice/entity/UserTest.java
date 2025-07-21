package org.microservices.userservice.entity;

import org.junit.jupiter.api.Test;
import org.microservices.userservice.enums.AuthProvider;
import org.microservices.userservice.enums.Role;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserBuilder() {
        // Arrange & Act
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .authProvider(AuthProvider.LOCAL)
                .providerId("local123")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Assert
        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("Test User", user.getFullName());
        assertEquals(AuthProvider.LOCAL, user.getAuthProvider());
        assertEquals("local123", user.getProviderId());
        assertEquals(Role.USER, user.getRole());
        assertTrue(user.isEnabled());
    }

    @Test
    void testUserDefaultValues() {
        // Arrange & Act
        User user = new User();

        // Assert
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getFullName());
        assertNull(user.getAuthProvider());
        assertNull(user.getProviderId());
        assertNull(user.getRole());
        assertFalse(user.isEnabled());
    }

    @Test
    void testUserSettersAndGetters() {
        // Arrange
        User user = new User();

        // Act
        user.setId(2L);
        user.setEmail("another@example.com");
        user.setPassword("anotherPassword");
        user.setFullName("Another User");
        user.setAuthProvider(AuthProvider.GITHUB);
        user.setProviderId("github123");
        user.setRole(Role.ADMIN);
        user.setEnabled(true);

        // Assert
        assertEquals(2L, user.getId());
        assertEquals("another@example.com", user.getEmail());
        assertEquals("anotherPassword", user.getPassword());
        assertEquals("Another User", user.getFullName());
        assertEquals(AuthProvider.GITHUB, user.getAuthProvider());
        assertEquals("github123", user.getProviderId());
        assertEquals(Role.ADMIN, user.getRole());
        assertTrue(user.isEnabled());
    }

    @Test
    void testUserEqualsAndHashCode() {
        // Arrange
        User user1 = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .authProvider(AuthProvider.LOCAL)
                .providerId("local123")
                .role(Role.USER)
                .enabled(true)
                .build();

        User user2 = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .authProvider(AuthProvider.LOCAL)
                .providerId("local123")
                .role(Role.USER)
                .enabled(true)
                .build();

        User user3 = User.builder()
                .id(2L)
                .email("different@example.com")
                .password("differentPassword")
                .fullName("Different User")
                .authProvider(AuthProvider.GITHUB)
                .providerId("github123")
                .role(Role.ADMIN)
                .enabled(false)
                .build();

        // Assert
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testUserToString() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .authProvider(AuthProvider.LOCAL)
                .providerId("local123")
                .role(Role.USER)
                .enabled(true)
                .build();

        // Act
        String userString = user.toString();

        // Assert
        assertTrue(userString.contains("id=1"));
        assertTrue(userString.contains("email=test@example.com"));
        assertTrue(userString.contains("password=password123"));
        assertTrue(userString.contains("fullName=Test User"));
        assertTrue(userString.contains("authProvider=LOCAL"));
        assertTrue(userString.contains("providerId=local123"));
        assertTrue(userString.contains("role=USER"));
        assertTrue(userString.contains("enabled=true"));
    }
}