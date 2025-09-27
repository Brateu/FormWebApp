package org.microservices.responseservice.service.validation;

import org.microservices.responseservice.dto.QuestionDefinitionDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * Validator for DATE question type. Accepts ISO-8601 yyyy-MM-dd strings or LocalDate.
 * Supports rules: minDate, maxDate (ISO), pastOnly, futureOnly, minAgeYears, maxAgeYears.
 */
@Component
public class DateAnswerValidator implements AnswerValidator {
    @Override
    public boolean supports(String type) {
        return type != null && type.equalsIgnoreCase("DATE");
    }

    @Override
    public void validate(QuestionDefinitionDto q, Object raw) {
        if (raw == null) {
            if (q.isRequired()) {
                throw new IllegalArgumentException("Answer required for question " + q.getId());
            }
            return;
        }

        LocalDate value = parseDate(raw, q.getId());
        if (value == null) {
            if (q.isRequired()) {
                throw new IllegalArgumentException("Answer required for question " + q.getId());
            }
            return;
        }

        Map<String, Object> rules = q.getValidationRules();
        if (rules != null) {
            String minDateStr = Rules.strRule(rules, "minDate");
            String maxDateStr = Rules.strRule(rules, "maxDate");
            Boolean pastOnly = Rules.boolRule(rules, "pastOnly");
            Boolean futureOnly = Rules.boolRule(rules, "futureOnly");
            Integer minAge = Rules.intRule(rules, "minAgeYears");
            Integer maxAge = Rules.intRule(rules, "maxAgeYears");

            if (minDateStr != null && !minDateStr.isBlank()) {
                try {
                    LocalDate min = LocalDate.parse(minDateStr.trim());
                    if (value.isBefore(min)) {
                        throw new IllegalArgumentException("Date before minimum for question " + q.getId());
                    }
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Invalid minDate rule format for question " + q.getId());
                }
            }
            if (maxDateStr != null && !maxDateStr.isBlank()) {
                try {
                    LocalDate max = LocalDate.parse(maxDateStr.trim());
                    if (value.isAfter(max)) {
                        throw new IllegalArgumentException("Date after maximum for question " + q.getId());
                    }
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Invalid maxDate rule format for question " + q.getId());
                }
            }

            LocalDate today = LocalDate.now();
            if (Boolean.TRUE.equals(pastOnly) && value.isAfter(today)) {
                throw new IllegalArgumentException("Date cannot be in the future for question " + q.getId());
            }
            if (Boolean.TRUE.equals(futureOnly) && value.isBefore(today)) {
                throw new IllegalArgumentException("Date must be in the future for question " + q.getId());
            }

            if (minAge != null && minAge > 0) {
                if (value.isAfter(today.minusYears(minAge))) {
                    throw new IllegalArgumentException("Minimum age is " + minAge + " years for question " + q.getId());
                }
            }
            if (maxAge != null && maxAge > 0) {
                if (value.isBefore(today.minusYears(maxAge))) {
                    throw new IllegalArgumentException("Maximum age is " + maxAge + " years for question " + q.getId());
                }
            }
        }
    }

    private LocalDate parseDate(Object raw, String qid) {
        try {
            if (raw instanceof LocalDate ld) {
                return ld;
            }
            if (raw instanceof LocalDateTime ldt) {
                return ldt.toLocalDate();
            }
            if (raw instanceof String s) {
                String v = s.trim();
                if (v.isEmpty()) return null;
                return LocalDate.parse(v);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format for question " + qid + ": " + e.getMessage());
        }
        throw new IllegalArgumentException("Date answer must be ISO string or LocalDate for question " + qid);
    }
}
