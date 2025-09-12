package org.microservices.responseservice.service.validation;

import org.microservices.responseservice.dto.QuestionDefinitionDto;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ChoiceAnswerValidator implements AnswerValidator {
    @Override
    public boolean supports(String type) {
        return type != null && (type.equalsIgnoreCase("CHOICE") || type.equalsIgnoreCase("SINGLE_CHOICE"));
    }

    @Override
    public void validate(QuestionDefinitionDto q, Object raw) {
        if (raw == null) {
            if (q.isRequired()) {
                throw new IllegalArgumentException("Answer required for question " + q.getId());
            }
            return;
        }
        String value = String.valueOf(raw);
        boolean ok = q.getOptions() != null && q.getOptions().stream()
                .anyMatch(o -> Objects.equals(o.getId(), value));
        if (!ok) {
            throw new IllegalArgumentException("Invalid option for question " + q.getId());
        }
    }
}
