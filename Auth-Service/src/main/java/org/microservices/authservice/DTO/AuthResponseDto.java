package org.microservices.authservice.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Represents the response DTO for authentication operations.
 * This class is used to encapsulate the JWT token and token type
 * returned as part of authentication-related API responses.
 */
@Data
@NoArgsConstructor
public class AuthResponseDto {
    private String token;
    private String tokenType = "Bearer";
    /**
     * Constructs an AuthResponseDto with the specified token.
     *
     * @param token The JWT token string used for authentication.
     */
    public AuthResponseDto(String token) {
        this.token = token;
    }
}