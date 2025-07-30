package org.microservices.formservice.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microservices.formservice.enums.QuestionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Question entity.
 * Used for transferring question data between layers.
 * Questions are the core components of a form that users respond to.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    /**
     * Unique identifier for the question.
     */
    private Long id;

    /**
     * The text content of the question.
     * Cannot be empty.
     */
    @NotBlank(message = "Question text cannot be empty")
    private String text;

    /**
     * The type of question (e.g., TEXT, MULTIPLE_CHOICE, etc.).
     * Determines how the question is displayed and answered.
     */
    @NotNull(message = "Question type is required")
    private QuestionType type;

    /**
     * Flag indicating whether the question must be answered.
     */
    private boolean required;

    /**
     * The position/order of the question within the form.
     * Also aliased as "position" for JSON serialization/deserialization.
     */
    @JsonAlias("position")
    private Integer orderIndex;

    /**
     * ID of the form this question belongs to.
     */
    private Long formId;

    /**
     * ID of the user who created or last modified the question.
     */
    private Long userId;

    /**
     * URL to an image associated with the question, if any.
     */
    private String imageUrl;

    /**
     * List of options available for this question.
     * Applicable for question types like MULTIPLE_CHOICE, CHECKBOX, etc.
     */
    private List<OptionDto> options = new ArrayList<>();
}