package org.microservices.responseservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microservices.responseservice.dto.AnsweredQuestionDto;
import org.microservices.responseservice.dto.QuestionDefinitionDto;
import org.microservices.responseservice.dto.ResponseDto;
import org.microservices.responseservice.service.validation.AnswerValidator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Offline validation service for response payloads using pluggable AnswerValidator implementations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ValidationService {

    private final List<AnswerValidator> validators;

    /**
     * Validate a response payload offline against provided question definitions and rules.
     * Throws IllegalArgumentException on any validation error.
     * @param responseDto response to validate
     */
    public void validateResponse(ResponseDto responseDto) {
        log.info("Validating response (offline) for form ID: {}", responseDto.getFormId());

        if (responseDto.getFormId() == null) {
            throw new IllegalArgumentException("Form ID cannot be null");
        }

        List<AnsweredQuestionDto> answers = responseDto.getAnsweredQuestions();
        boolean isDraft = "DRAFT".equalsIgnoreCase(responseDto.getStatus());
        if ((answers == null || answers.isEmpty()) && !isDraft) {
            throw new IllegalArgumentException("Answers cannot be null or empty for SUBMITTED responses");
        }

        List<QuestionDefinitionDto> defs = Optional.ofNullable(responseDto.getQuestionDefinitions())
                .orElseThrow(() -> new IllegalArgumentException("questionDefinitions are required in offline validation mode"));

        Map<String, QuestionDefinitionDto> defsById = defs.stream()
                .collect(Collectors.toMap(QuestionDefinitionDto::getId, d -> d, (a, b) -> a));

        if (answers != null) {
            Set<String> seen = new HashSet<>();
            for (AnsweredQuestionDto a : answers) {
                if (a.getQuestionId() == null || a.getQuestionId().isBlank()) {
                    throw new IllegalArgumentException("Each answer must have a questionId");
                }
                QuestionDefinitionDto q = defsById.get(a.getQuestionId());
                if (q == null) {
                    throw new IllegalArgumentException("Unknown questionId in answers: " + a.getQuestionId());
                }
                if (!seen.add(a.getQuestionId())) {
                    throw new IllegalArgumentException("Duplicate answer for questionId: " + a.getQuestionId());
                }

                if (q.getType() == null || a.getType() == null || !q.getType().equalsIgnoreCase(a.getType())) {
                    throw new IllegalArgumentException("Type mismatch for question " + q.getId() + ": expected " + q.getType() + ", got " + a.getType());
                }

                AnswerValidator validator = validators.stream()
                        .filter(v -> v.supports(q.getType()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No validator for type: " + q.getType()));
                validator.validate(q, a.getValue());
            }
        }

        if (!isDraft) {
            for (QuestionDefinitionDto q : defs) {
                if (q.isRequired()) {
                    boolean present = answers != null && answers.stream()
                            .anyMatch(a -> q.getId().equals(a.getQuestionId()) && !isEmpty(a.getValue()));
                    if (!present) {
                        throw new IllegalArgumentException("Required question missing: " + q.getId());
                    }
                }
            }
        }

        log.debug("Validation passed (offline) for form {}", responseDto.getFormId());
    }

    /**
     * Utility to check if a value is logically empty for validation purposes.
     * @param v value
     * @return true if null/blank/empty collection or map
     */
    private boolean isEmpty(Object v) {
        return switch (v) {
            case null -> true;
            case String s -> s.isBlank();
            case Collection<?> c -> c.isEmpty();
            case Map<?, ?> m -> m.isEmpty();
            default -> false;
        };
    }
}