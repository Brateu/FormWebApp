package org.microservices.userservice.exceptions;

/**
 * Custom exception class for authentication-related errors in the authentication service.
 *
 * This exception is a specialized {@code RuntimeException} intended for scenarios
 * where errors occur during authentication, such as user not found, invalid credentials,
 * unsupported principal type, or any other authentication-specific conditions.
 *
 * Instances of this exception typically carry a message detailing the associated error,
 * which can be used for logging or client communication purposes.
 */
public class UserServiceException extends RuntimeException {
    public UserServiceException(String message) {
        super(message);
    }
}