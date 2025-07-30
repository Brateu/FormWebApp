package org.microservices.formservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.microservices.formservice.enums.Visibility;
import org.microservices.formservice.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a form.
 * A form is a collection of questions that can be answered by users.
 * Forms can have different statuses, visibility settings, and collaborators.
 */
@Entity
@Table(name = "forms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form {

    /**
     * Unique identifier for the form.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the form.
     */
    private String name;

    /**
     * Description of the form.
     */
    private String description;

    /**
     * Flag indicating whether anonymous responses are allowed.
     */
    @Column(name = "allow_anonymous")
    private boolean allowAnonymous;

    /**
     * Maximum number of responses allowed for this form.
     * A value of 0 indicates no limit.
     */
    @Column(nullable = false)
    private int responseLimit = 0;

    /**
     * Flag indicating whether the form is locked for editing.
     */
    @Column(name = "is_locked")
    private boolean locked;

    /**
     * Date and time when the form was created.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Date and time when the form was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * ID of the user who created the form.
     */
    private Long createdBy;

    /**
     * Current status of the form (DRAFT, PUBLISHED, CLOSED).
     */
    private Status status;

    /**
     * Visibility setting of the form (PUBLIC, PRIVATE).
     */
    private Visibility visibility;

    /**
     * List of questions contained in this form.
     */
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Question> questions;

    /**
     * List of collaborators who have access to this form.
     */
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Collaborator> collaborators;

}