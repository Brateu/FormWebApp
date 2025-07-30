package org.microservices.formservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microservices.formservice.enums.CollaboratorRole;

/**
 * Data Transfer Object for Collaborator entity.
 * Used for transferring collaborator data between layers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollaboratorDto {
    /**
     * Unique identifier for the collaborator.
     */
    private Long id;

    /**
     * ID of the user who is the collaborator.
     */
    private Long userId;

    /**
     * Email of the collaborator, used for lookup if userId is not provided.
     */
    private String email;

    /**
     * Role of the collaborator (VIEWER or EDITOR).
     */
    private CollaboratorRole role;

    /**
     * ID of the form the collaborator is associated with.
     */
    private Long formId;
}