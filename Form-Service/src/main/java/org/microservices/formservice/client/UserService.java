package org.microservices.formservice.client;

/**
 * Interface for communication with the User-Service.
 * This interface defines methods for retrieving user information from the User-Service.
 */
public interface UserService {
    
    /**
     * Retrieves a user ID by email.
     *
     * @param email the email of the user to look up
     * @return the user ID if found, or null if not found
     * @throws org.microservices.formservice.exception.UserNotFoundException if the user is not found
     */
    Long getUserIdByEmail(String email);
}