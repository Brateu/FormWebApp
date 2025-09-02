package org.microservices.responseservice.repository;

import org.microservices.responseservice.model.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing form responses in MongoDB.
 */
@Repository
public interface ResponseRepository extends MongoRepository<Response, String> {
    
    /**
     * Find all responses for a specific form.
     * 
     * @param formId The form ID
     * @return List of responses
     */
    List<Response> findByFormId(Long formId);
    
    /**
     * Find all responses for a specific form with pagination.
     * 
     * @param formId The form ID
     * @param pageable Pagination information
     * @return Page of responses
     */
    Page<Response> findByFormId(Long formId, Pageable pageable);
    
    /**
     * Find all responses submitted by a specific user.
     * 
     * @param userId The user ID
     * @return List of responses
     */
    List<Response> findByUserId(Long userId);
    
    /**
     * Find all responses submitted by a specific user with pagination.
     * 
     * @param userId The user ID
     * @param pageable Pagination information
     * @return Page of responses
     */
    Page<Response> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Find all responses for a specific form submitted by a specific user.
     * 
     * @param formId The form ID
     * @param userId The user ID
     * @return List of responses
     */
    List<Response> findByFormIdAndUserId(Long formId, Long userId);
    
    /**
     * Find all responses for a specific form with a specific status.
     * 
     * @param formId The form ID
     * @param status The response status
     * @return List of responses
     */
    List<Response> findByFormIdAndStatus(Long formId, String status);
    
    /**
     * Find all responses for a specific form with a specific status and pagination.
     * 
     * @param formId The form ID
     * @param status The response status
     * @param pageable Pagination information
     * @return Page of responses
     */
    Page<Response> findByFormIdAndStatus(Long formId, String status, Pageable pageable);
    
    /**
     * Find all responses submitted within a date range.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return List of responses
     */
    List<Response> findBySubmittedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all responses submitted within a date range with pagination.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @param pageable Pagination information
     * @return Page of responses
     */
    Page<Response> findBySubmittedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find all responses for a specific form submitted within a date range.
     * 
     * @param formId The form ID
     * @param startDate The start date
     * @param endDate The end date
     * @return List of responses
     */
    List<Response> findByFormIdAndSubmittedAtBetween(Long formId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all responses for a specific form submitted within a date range with pagination.
     * 
     * @param formId The form ID
     * @param startDate The start date
     * @param endDate The end date
     * @param pageable Pagination information
     * @return Page of responses
     */
    Page<Response> findByFormIdAndSubmittedAtBetween(Long formId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find the latest draft response for a specific form and user.
     * 
     * @param formId The form ID
     * @param userId The user ID
     * @param status The response status (should be "DRAFT")
     * @return Optional containing the latest draft response, if any
     */
    Optional<Response> findFirstByFormIdAndUserIdAndStatusOrderByUpdatedAtDesc(Long formId, Long userId, String status);
    
    /**
     * Custom query to find responses containing specific answer data.
     * 
     * @param formId The form ID
     * @param questionId The question ID
     * @param answer The answer to search for
     * @param pageable Pagination information
     * @return Page of responses
     */
    @Query("{'formId': ?0, 'responseData.?1': ?2}")
    Page<Response> findByFormIdAndQuestionAnswer(Long formId, String questionId, Object answer, Pageable pageable);
}