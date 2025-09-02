package org.microservices.responseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for validation results.
 * Used for transferring validation results between services and to clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    
    /**
     * Whether the validation was successful.
     */
    private boolean valid;
    
    /**
     * List of validation errors.
     */
    private List<ValidationError> errors = new ArrayList<>();
    
    /**
     * Creates a successful validation result.
     * 
     * @return A successful validation result
     */
    public static ValidationResult success() {
        return new ValidationResult(true, new ArrayList<>());
    }
    
    /**
     * Creates a failed validation result with the given errors.
     * 
     * @param errors The validation errors
     * @return A failed validation result
     */
    public static ValidationResult failure(List<ValidationError> errors) {
        return new ValidationResult(false, errors);
    }
    
    /**
     * Adds an error to the validation result.
     * 
     * @param error The validation error
     */
    public void addError(ValidationError error) {
        this.valid = false;
        this.errors.add(error);
    }
    
    /**
     * Data Transfer Object for validation errors.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        
        /**
         * The field that failed validation.
         */
        private String field;
        
        /**
         * The error message.
         */
        private String message;
    }
}