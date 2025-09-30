package org.microservices.formservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.microservices.formservice.enums.CollaboratorRole;

/**
 * Entity representing a collaborator on a form.
 * A collaborator is a user who has been granted access to a form
 * with specific permissions (view or edit).
 */
@Entity
@Table(name = "collaborators")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collaborator {
    //

    /**
     * Unique identifier for the collaborator.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The form that this collaboration is for.
     * Many collaborators can be associated with one form.
     */
    @ManyToOne
    @JoinColumn(name = "form_id", nullable = false)
    @JsonBackReference
    private Form form;

    /**
     * ID of the user who is the collaborator.
     */
    private Long userId;

    /**
     * Role of the collaborator, determining their permissions.
     * Can be VIEWER (read-only access) or EDITOR (can modify the form).
     */
    @Enumerated(EnumType.STRING)
    private CollaboratorRole role;
}