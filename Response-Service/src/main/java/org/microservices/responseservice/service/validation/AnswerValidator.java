package org.microservices.responseservice.service.validation;

import org.microservices.responseservice.dto.QuestionDefinitionDto;

/**
 * Strategy interface for validating an answer for a given question definition.
 */
public interface AnswerValidator {
    /**
     * Whether this validator supports the given question type (case-insensitive).
     * @param type question type
     * @return true if supported
     */
    boolean supports(String type);

    /**
     * Validate the raw value for the provided question definition.
     * Implementations should throw IllegalArgumentException on validation failure.
     * @param question definition describing constraints and metadata
     * @param rawValue value to validate
     */
    void validate(QuestionDefinitionDto question, Object rawValue);
}
