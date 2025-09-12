package org.microservices.responseservice.service.validation;

import org.microservices.responseservice.dto.QuestionDefinitionDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MultiChoiceAnswerValidator implements AnswerValidator {
    @Override
    public boolean supports(String type) {
        return type != null && (type.equalsIgnoreCase("MULTI_CHOICE") || type.equalsIgnoreCase("MULTIPLE_CHOICE"));
    }

    @Override
    public void validate(QuestionDefinitionDto q, Object raw) {
        if (raw == null) {
            if (q.isRequired()) {
                throw new IllegalArgumentException("Answer required for question " + q.getId());
            }
            return;
        }
        if (!(raw instanceof Collection<?> col)) {
            throw new IllegalArgumentException("Multi-choice answer must be an array for question " + q.getId());
        }
        Set<String> allowed = q.getOptions() == null ? new HashSet<>() : q.getOptions().stream()
                .map(o -> o.getId())
                .collect(Collectors.toSet());
        for (Object item : col) {
            String id = String.valueOf(item);
            if (!allowed.contains(id)) {
                throw new IllegalArgumentException("Invalid option in multi-choice for question " + q.getId());
            }
        }
        Map<String, Object> rules = q.getValidationRules();
        if (rules != null) {
            Integer minSel = Rules.intRule(rules, "minSelections");
            Integer maxSel = Rules.intRule(rules, "maxSelections");
            int size = col.size();
            if (minSel != null && size < minSel) {
                throw new IllegalArgumentException("Too few selections for question " + q.getId());
            }
            if (maxSel != null && size > maxSel) {
                throw new IllegalArgumentException("Too many selections for question " + q.getId());
            }
        }
    }
}
