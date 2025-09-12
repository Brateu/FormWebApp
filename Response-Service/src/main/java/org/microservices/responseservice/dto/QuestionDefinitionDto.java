package org.microservices.responseservice.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for defining a question within a form.
 * Provides the structure for representing a question's attributes, such as its content,
 * type, required status, associated options, and validation rules.
 *
 * This class is typically used in scenarios where forms and responses
 * require detailed metadata about the questions, enabling features
 * like validation, dynamic rendering, and consistent data collection.
 */
@Data
public class QuestionDefinitionDto {
    /**
     * The unique identifier for the question definition.
     * Used to correlate questions within forms and responses.
     */
    private String id;
    /**
     * The text associated with a question definition.
     * Represents the content or prompt to be displayed to the user.
     */
    private String text;
    /**
     * The type of the question.
     * Defines the format or permissible values for the question's response.
     * Common types include:
     * - TEXT: A single line of text.
     * - LONG_TEXT: A multiline text input.
     * - CHOICE: A single choice from predefined options.
     * - MULTI_CHOICE: Multiple selectable options.
     * - DATE: A date value.
     */
    private String type; // TEXT, LONG_TEXT, CHOICE, MULTI_CHOICE, DATE, etc.
    /**
     * Indicates whether the question is required to be answered.
     * If true, the question must be answered; otherwise, it is optional.
     */
    private boolean required;
    /**
     * List of options associated with the question.
     * Each option is represented by an instance of {@link OptionDto}, which contains
     * the option's unique identifier and optional label text.
     *
     * This list is relevant for questions of type CHOICE or MULTI_CHOICE, where
     * predefined options are presented to the user for selection.
     *
     * Examples of use cases include single-select questions where a user can choose
     * one option or multi-select questions where multiple options can be selected.
     * If the question type does not require options (e.g., TEXT, LONG_TEXT), this
     * list may remain empty.
     */
    private List<OptionDto> options = new ArrayList<>();
    /**
     * A map containing validation rules for a question's response.
     * The keys in this map represent specific validation criteria,
     * and the corresponding values define the requirements or constraints
     * that must be satisfied to pass validation.
     *
     * Examples of validation rules include:
     * - "minLength": Specifies the minimum number of characters for a text response.
     * - "maxLength": Specifies the maximum number of characters for a text response.
     * - "pattern": Defines a regex pattern that the response must match.
     * - "minAgeYears": Indicates the minimum age, in years, required for the response.
     *
     * This map provides flexibility to define and configure rules based
     * on the question type and validation requirements.
     */
    private Map<String, Object> validationRules;
}
