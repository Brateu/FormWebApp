package org.microservices.responseservice.service.validation;

import org.microservices.responseservice.dto.QuestionDefinitionDto;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Validator for TEXT and LONG_TEXT question types.
 */
@Component
public class TextAnswerValidator implements AnswerValidator {
    /**
     * Supports TEXT and LONG_TEXT types.
     */
    @Override
    public boolean supports(String type) {
        return type != null && (type.equalsIgnoreCase("TEXT") || type.equalsIgnoreCase("LONG_TEXT"));
    }

    @Override
    public void validate(QuestionDefinitionDto q, Object raw) {
        if (raw == null) {
            if (q.isRequired()) {
                throw new IllegalArgumentException("Answer required for question " + q.getId());
            }
            return;
        }
        if (!(raw instanceof String value)) {
            throw new IllegalArgumentException("Text answer must be a string for question " + q.getId());
        }
        Map<String, Object> rules = q.getValidationRules();
        if (rules != null) {
            Boolean trim = Rules.boolRule(rules, "trim");
            if (Boolean.TRUE.equals(trim)) {
                value = value.trim();
            }
            Integer min = Rules.intRule(rules, "minLength");
            Integer max = Rules.intRule(rules, "maxLength");
            String pattern = Rules.strRule(rules, "pattern");

            if (min != null && value.length() < min) {
                throw new IllegalArgumentException("Text shorter than minLength for question " + q.getId());
            }
            if (max != null && value.length() > max) {
                throw new IllegalArgumentException("Text longer than maxLength for question " + q.getId());
            }
            if (pattern != null && !pattern.isBlank() && !value.matches(pattern)) {
                throw new IllegalArgumentException("Text does not match pattern for question " + q.getId());
            }
        }
        if (q.isRequired() && value.isBlank()) {
            throw new IllegalArgumentException("Required question has blank text: " + q.getId());
        }
    }
}
