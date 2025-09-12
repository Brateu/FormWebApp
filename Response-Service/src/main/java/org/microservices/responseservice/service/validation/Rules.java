package org.microservices.responseservice.service.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class Rules {
    private Rules() {}

    public static Integer intRule(Map<String, Object> rules, String key) {
        if (rules == null) return null;
        Object v = rules.get(key);
        switch (v) {
            case null -> {
                return null;
            }
            case Integer i -> {
                return i;
            }
            case Number n -> {
                return n.intValue();
            }
            case String s when !s.isBlank() -> {
                try {
                    return Integer.valueOf(s.trim());
                } catch (NumberFormatException ignored) {
                }
            }
            default -> {
            }
        }
        return null;
    }

    public static Double doubleRule(Map<String, Object> rules, String key) {
        if (rules == null) return null;
        Object v = rules.get(key);
        if (v == null) return null;
        if (v instanceof Number n) return n.doubleValue();
        if (v instanceof String s && !s.isBlank()) {
            try { return Double.valueOf(s.trim()); } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    public static List<Double> doubleListRule(Map<String, Object> rules, String key) {
        List<Double> out = new ArrayList<>();
        if (rules == null) return out;
        Object v = rules.get(key);
        if (v == null) return out;
        if (v instanceof Collection<?> col) {
            for (Object o : col) {
                if (o instanceof Number n) out.add(n.doubleValue());
                else if (o instanceof String s && !s.isBlank()) {
                    try { out.add(Double.valueOf(s.trim())); } catch (NumberFormatException ignored) {}
                }
            }
            return out;
        }
        if (v instanceof String s && !s.isBlank()) {
            // support comma-separated values
            String[] parts = s.split(",");
            for (String p : parts) {
                String t = p.trim();
                if (!t.isEmpty()) {
                    try { out.add(Double.valueOf(t)); } catch (NumberFormatException ignored) {}
                }
            }
        }
        return out;
    }

    public static Boolean boolRule(Map<String, Object> rules, String key) {
        if (rules == null) return null;
        Object v = rules.get(key);
        return switch (v) {
            case null -> null;
            case Boolean b -> b;
            case String s -> Boolean.valueOf(s.trim());
            default -> null;
        };
    }

    public static String strRule(Map<String, Object> rules, String key) {
        if (rules == null) return null;
        Object v = rules.get(key);
        return v == null ? null : String.valueOf(v);
    }
}
