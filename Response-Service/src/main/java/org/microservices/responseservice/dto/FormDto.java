package org.microservices.responseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Form.
 * Used for transferring form data between services.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormDto {
    
    /**
     * The form ID.
     */
    private Long id;
    
    /**
     * The form title.
     */
    private String title;
    
    /**
     * The form description.
     */
    private String description;
    
    /**
     * The form status (DRAFT, PUBLISHED, CLOSED).
     */
    private String status;
    
    /**
     * The form visibility (PUBLIC, PRIVATE).
     */
    private String visibility;
    
    /**
     * The ID of the user who created the form.
     */
    private Long createdBy;
    
    /**
     * The creation timestamp.
     */
    private LocalDateTime createdAt;
    
    /**
     * The last update timestamp.
     */
    private LocalDateTime updatedAt;
}