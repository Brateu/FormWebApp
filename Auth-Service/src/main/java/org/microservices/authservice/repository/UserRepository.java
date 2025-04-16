package org.microservices.authservice.repository;

import org.microservices.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository is a Spring Data JPA repository interface for managing {@link User} entities.
 * It provides methods to interact with the database, such as finding users by email
 * and checking the existence of a user based on email.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user to be retrieved
     * @return an {@code Optional} containing the user if found, or an empty {@code Optional} if no user exists with the given email
     */
    Optional<User> findByEmail(String email);
    /**
     * Checks whether a user exists in the database with the specified email address.
     *
     * @param email the email address to be checked for existence
     * @return {@code true} if a user with the specified email exists, {@code false} otherwise
     */
    boolean existsByEmail(String email);

}