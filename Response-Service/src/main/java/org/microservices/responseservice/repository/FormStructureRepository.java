package org.microservices.responseservice.repository;

import org.microservices.responseservice.model.FormStructure;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing form structures in MongoDB.
 */
@Repository
public interface FormStructureRepository extends MongoRepository<FormStructure, String> {
    
    /**
     * Find a form structure by its form ID.
     * 
     * @param formId The form ID
     * @return Optional containing the form structure, if found
     */
    Optional<FormStructure> findByFormId(Long formId);
    
    /**
     * Check if a form structure exists for a specific form.
     * 
     * @param formId The form ID
     * @return True if a form structure exists, false otherwise
     */
    boolean existsByFormId(Long formId);
    
    /**
     * Delete a form structure by its form ID.
     * 
     * @param formId The form ID
     */
    void deleteByFormId(Long formId);
}