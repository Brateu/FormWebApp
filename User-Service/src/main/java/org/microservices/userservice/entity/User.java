package org.microservices.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microservices.userservice.enums.AuthProvider;
import org.microservices.userservice.enums.Role;

/**
 * Represents a User entity in the application.
 * This entity is mapped to the "users" table in the database.
 * It contains details about the user such as email, password, full name, authentication provider, provider ID, and role.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private AuthProvider authProvider;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled = false;

}