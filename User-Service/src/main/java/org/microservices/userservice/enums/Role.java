package org.microservices.userservice.enums;

/**
 * Enum representing the roles or authorities a user can have within the application.
 *
 * This enum provides predefined constants to distinguish between different levels
 * of access a user may have. These roles can be used to enforce access control
 * throughout the application using role-based security configurations.
 */
public enum Role {
    /**
     * Standard user role with basic privileges.
     * Users with this role can access their own data and use the basic features of the application.
     */
    USER,

    /**
     * Administrator role with elevated privileges.
     * Users with this role have access to administrative functions and can manage other users.
     */
    ADMIN
}