package org.microservices.formservice.DTO;

import lombok.*;
import org.microservices.formservice.enums.Visibility;
import org.microservices.formservice.enums.Status;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Form entity.
 * Used for transferring form data between layers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormDto {
    /**
     * Unique identifier for the form.
     */
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
    private boolean allowAnonymous;

    /**
     * Maximum number of responses allowed for this form.
     * A value of 0 indicates no limit.
     */
    private int responseLimit;

    /**
     * Flag indicating whether the form is locked for editing.
     */
    private boolean locked;

    /**
     * Date and time when the form was created.
     */
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
}