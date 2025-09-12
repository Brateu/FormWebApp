package org.microservices.responseservice.dto;

import lombok.Data;

@Data
public class AnsweredQuestionDto {
    /**
     * The unique identifier of the question being answered.
     * This ID is used to correlate the user's answer with the corresponding
     * question definition in a form or survey, enabling proper linking
     * and validation of the response.
     */
    private String questionId;
    /**
     * The type of question or response.
     * This field defines the format or nature of the user's response to the question.
     * Common types include:
     * - TEXT: A single line text response.
     * - LONG_TEXT: A multiline text response.
     * - CHOICE: A single selection from predefined options.
     * - MULTI_CHOICE: Multiple selections from predefined options.
     * - DATE: A date input.
     * The type determines input validation, rendering, and handling of the response.
     */
    private String type;
    /**
     * The response or answer provided by a user for a question within a form or survey.
     * The value is polymorphic and its type may vary depending on the question type:
     * - For TEXT or LONG_TEXT questions: a String representing the user's textual input.
     * - For CHOICE questions: a String or an OptionDto representing the selected option ID or object.
     * - For MULTI_CHOICE questions: a List<String> or List<OptionDto> representing multiple selected options.
     * - For DATE questions: a LocalDate or formatted string representing the chosen date.
     *
     * This field captures the user's response and ensures compatibility with
     * varying question types, allowing for flexible and dynamic data handling in responses.
     */
    private Object value;
}
