package org.microservices.responseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for representing an option within a question.
 * Typically used in scenarios involving predefined choices in forms or surveys.
 *
 * This class encapsulates the details of an individual option, which
 * can be associated with question types like CHOICE or MULTI_CHOICE.
 *
 * Options are utilized to present users with selectable answers to a question,
 * where each option is uniquely identifiable and may have an associated
 * descriptive label or text.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionDto {
    /**
     * The unique identifier for the option.
     * Used to distinguish this option from others in the same context.
     */
    private String id;
    /**
     * The optional label text for the option.
     * Represents additional descriptive or display information associated with the option.
     */
    private String text;
}
