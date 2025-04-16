package org.microservices.authservice.DTO;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a simplified user entity.
 * This class is used to transfer user-related data between different application layers,
 * particularly in scenarios where only a subset of the user details is required.
 *
 * Fields include:
 * - id: The unique identifier for the user.
 * - email: The email address associated with the user.
 * - fullName: The full name of the user.
 *
 * Commonly used in services and APIs where user details need to be encapsulated and transferred
 * without including sensitive or unnecessary information.
 */
@Data
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
}