package org.microservices.formservice.exception;

/**
 * Exception thrown when a user cannot be found by their email address.
 */
public class UserNotFoundException extends RuntimeException {
    
    /**
     * Constructs a new UserNotFoundException with the specified email.
     *
     * @param email The email address that could not be found
     */
    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}