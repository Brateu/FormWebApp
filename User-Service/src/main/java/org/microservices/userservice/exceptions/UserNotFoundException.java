package org.microservices.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user cannot be found by their email or ID.
 * This exception is annotated with {@code @ResponseStatus(HttpStatus.NOT_FOUND)}
 * to ensure that Spring returns a 404 Not Found status when this exception is thrown.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified email.
     *
     * @param email The email address that could not be found
     * @return A new UserNotFoundException
     */
    public static UserNotFoundException withEmail(String email) {
        return new UserNotFoundException("User not found with email: " + email);
    }

    /**
     * Constructs a new UserNotFoundException with the specified ID.
     *
     * @param id The user ID that could not be found
     * @return A new UserNotFoundException
     */
    public static UserNotFoundException withId(Long id) {
        return new UserNotFoundException("User not found with id: " + id);
    }

    /**
     * Constructs a new UserNotFoundException with the specified message.
     *
     * @param message The detail message
     */
    private UserNotFoundException(String message) {
        super(message);
    }
}