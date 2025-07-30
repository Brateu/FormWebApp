package org.microservices.userservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the data transfer object used for a login request.
 * This class encapsulates the email and password required for
 * authenticating a user.
 *
 * It is commonly used in API endpoints that handle
 * user login functionality.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    /**
     * The email address of the user attempting to log in.
     * Used as the username for authentication.
     */
    private String email;

    /**
     * The password of the user attempting to log in.
     * Will be validated against the stored password hash.
     */
    private String password;
}