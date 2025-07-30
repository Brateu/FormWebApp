package org.microservices.userservice.DTO;

import lombok.Data;
import org.microservices.userservice.enums.AuthProvider;

/**
 * Data Transfer Object (DTO) representing a simplified user entity.
 * This class is used to transfer user-related data between different application layers,
 * particularly in scenarios where only a subset of the user details is required.
 *
 * Fields include:
 * - id: The unique identifier for the user.
 * - email: The email address associated with the user.
 * - fullName: The full name of the user.
 * - authProvider: The authentication provider used by the user (LOCAL, GOOGLE, GITHUB).
 *
 * Commonly used in services and APIs where user details need to be encapsulated and transferred
 * without including sensitive or unnecessary information.
 */
@Data
public class UserDto {
    /**
     * The unique identifier for the user.
     */
    private Long id;

    /**
     * The email address associated with the user.
     * Used as the username for authentication.
     */
    private String email;

    /**
     * The full name of the user.
     */
    private String fullName;

    /**
     * The authentication provider used by the user.
     * Can be LOCAL, GOOGLE, GITHUB, etc.
     */
    private AuthProvider authProvider;
}