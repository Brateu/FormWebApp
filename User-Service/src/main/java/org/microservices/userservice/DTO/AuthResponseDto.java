package org.microservices.userservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Represents the response DTO for authentication operations.
 * This class is used to encapsulate the JWT token and token type
 * returned as part of authentication-related API responses.
 * It can also include an error message when authentication fails.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {
    private String token;
    private String tokenType = "Bearer";
    private String errorMessage;
    private Long userId;
    /**
     * Constructs an AuthResponseDto with the specified token.
     *
     * @param token The JWT token string used for authentication.
     */
    public AuthResponseDto(String token) {
        this.token = token;
    }

    /**
     * Constructs an AuthResponseDto with the specified error message.
     * This constructor is used for error responses where no token is available.
     *
     * @param errorMessage The error message describing the authentication failure.
     */
    public AuthResponseDto(String errorMessage, boolean isError) {
        this.errorMessage = errorMessage;
    }
}