package org.microservices.formservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Option entity.
 * Used for transferring option data between layers.
 * Options are choices that can be selected for a question.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionDto {
    /**
     * Unique identifier for the option.
     */
    private Long id;

    /**
     * Text content of the option.
     */
    private String text;

    /**
     * URL to an image associated with the option, if any.
     */
    private String imageUrl;

    /**
     * ID of the question this option belongs to.
     */
    private Long questionId;
}