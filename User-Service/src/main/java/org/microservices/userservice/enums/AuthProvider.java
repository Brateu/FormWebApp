package org.microservices.userservice.enums;

/**
 * Enum representing the available authentication providers for the application.
 *
 * This enum is used to distinguish between various third-party or local
 * authentication systems supported by the service. It indicates the source
 * of authentication for a given user.
 */
public enum AuthProvider {
    /**
     * Represents authentication using the application's local authentication system.
     * Users with this provider have registered directly with the application.
     */
    LOCAL,

    /**
     * Represents authentication using Google OAuth2.
     * Users with this provider have authenticated using their Google account.
     */
    GOOGLE,

    /**
     * Represents authentication using GitHub OAuth2.
     * Users with this provider have authenticated using their GitHub account.
     */
    GITHUB
}