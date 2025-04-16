package org.microservices.authservice.enums;

/**
 * Enum representing the roles or authorities a user can have within the application.
 *
 * This enum provides predefined constants to distinguish between different levels
 * of access a user may have. These roles can be used to enforce access control
 * throughout the application using role-based security configurations.
 */
public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}