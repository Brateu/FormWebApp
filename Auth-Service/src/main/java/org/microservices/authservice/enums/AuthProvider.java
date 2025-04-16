package org.microservices.authservice.enums;

/**
 * Enum representing the available authentication providers for the application.
 *
 * This enum is used to distinguish between various third-party or local
 * authentication systems supported by the service. It indicates the source
 * of authentication for a given user.
 */
public enum AuthProvider {
    LOCAL,
    GOOGLE,
    GITHUB
}