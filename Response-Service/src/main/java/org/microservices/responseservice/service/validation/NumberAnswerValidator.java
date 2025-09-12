package org.microservices.responseservice.service.validation;

import org.microservices.responseservice.dto.QuestionDefinitionDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class NumberAnswerValidator implements AnswerValidator {
    @Override
    public boolean supports(String type) {
        if (type == null) return false;
        String t = type.trim().toUpperCase();
        return t.equals("NUMBER") || t.equals("INTEGER") || t.equals("DECIMAL") || t.equals("RATING") || t.equals("SCALE");
    }

    @Override
    public void validate(QuestionDefinitionDto q, Object raw) {
        if (raw == null) {
            if (q.isRequired()) {
                throw new IllegalArgumentException("Answer required for question " + q.getId());
            }
            return;
        }

        Double value = parseNumber(raw, q.getId());

        Map<String, Object> rules = q.getValidationRules();
        boolean integerOnly = false;
        String t = q.getType() == null ? null : q.getType().trim().toUpperCase();
        if ("INTEGER".equals(t)) {
            integerOnly = true;
        } else if (rules != null) {
            Boolean io = Rules.boolRule(rules, "integerOnly");
            integerOnly = Boolean.TRUE.equals(io);
        }

        if (integerOnly && value != null) {
            if (Math.floor(value) != value) {
                throw new IllegalArgumentException("Value must be an integer for question " + q.getId());
            }
        }

        if (rules != null) {
            // allowedNumbers list takes precedence
            List<Double> allowed = Rules.doubleListRule(rules, "allowedNumbers");
            if (!allowed.isEmpty()) {
                if (!containsNumber(allowed, value)) {
                    throw new IllegalArgumentException("Value not in allowed list for question " + q.getId());
                }
                return; // allowed list satisfied; skip other checks
            }

            Double min = Rules.doubleRule(rules, "min");
            Double max = Rules.doubleRule(rules, "max");
            Double step = Rules.doubleRule(rules, "step");

            if (min != null && value < min) {
                throw new IllegalArgumentException("Value below minimum for question " + q.getId());
            }
            if (max != null && value > max) {
                throw new IllegalArgumentException("Value above maximum for question " + q.getId());
            }

            if (step != null && step > 0) {
                // Align to arithmetic progression starting at min (or 0 if min is null)
                double origin = (min != null) ? min : 0.0;
                double diff = (value - origin);
                double rem = Math.abs(diff % step);
                double eps = 1e-9;
                boolean matches = rem < eps || Math.abs(rem - step) < eps;
                if (!matches) {
                    throw new IllegalArgumentException("Value does not align to step for question " + q.getId());
                }
            }
        }
    }

    private Double parseNumber(Object raw, String qid) {
        if (raw instanceof Number n) {
            return n.doubleValue();
        }
        if (raw instanceof String s) {
            String str = s.trim();
            if (str.isEmpty()) {
                return null; // treat empty as null; required check already handled
            }
            try {
                return Double.valueOf(str);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid numeric value for question " + qid);
            }
        }
        throw new IllegalArgumentException("Numeric answer must be a number or numeric string for question " + qid);
    }

    private boolean containsNumber(List<Double> list, Double value) {
        if (value == null) return false;
        double eps = 1e-9;
        for (Double d : list) {
            if (d == null) continue;
            if (Math.abs(d - value) < eps) return true;
        }
        return false;
    }
}
