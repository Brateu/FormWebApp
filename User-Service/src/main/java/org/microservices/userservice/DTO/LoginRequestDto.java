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
    private String email;
    private String password;
}