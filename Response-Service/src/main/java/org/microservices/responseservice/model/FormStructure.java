package org.microservices.responseservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MongoDB document for storing form structures.
 * Used for validating responses against the form structure.
 */
@Document(collection = "form_structures")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormStructure {
    
    /**
     * The form structure ID.
     */
    @Id
    private String id;
    
    /**
     * The ID of the form this structure is for.
     * Indexed for faster queries.
     */
    @Indexed(unique = true)
    private Long formId;
    
    /**
     * The list of questions in the form.
     */
    private List<Question> questions = new ArrayList<>();
    
    /**
     * The creation timestamp.
     */
    private LocalDateTime createdAt;
    
    /**
     * The last update timestamp.
     */
    private LocalDateTime updatedAt;
    
    /**
     * Pre-persist hook to set timestamps.
     */
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }
    
    /**
     * Represents a question in a form.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Question {
        
        /**
         * The question ID.
         */
        private String id;
        
        /**
         * The question text.
         */
        private String text;
        
        /**
         * The question type (e.g., TEXT, NUMBER, CHOICE, MULTI_CHOICE, DATE, etc.).
         */
        private String type;
        
        /**
         * Whether the question is required.
         */
        private boolean required;
        
        /**
         * The list of options for choice questions.
         */
        private List<Option> options = new ArrayList<>();
        
        /**
         * Additional validation rules for the question.
         */
        private Map<String, Object> validationRules;
    }
    
    /**
     * Represents an option for a choice question.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option {
        
        /**
         * The option ID.
         */
        private String id;
        
        /**
         * The option text.
         */
        private String text;
    }
}