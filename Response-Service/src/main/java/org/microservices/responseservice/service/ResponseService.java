package org.microservices.responseservice.service;

import org.microservices.responseservice.dto.ResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for managing form responses.
 */
public interface ResponseService {
    
    /**
     * Create a new response.
     * 
     * @param responseDto The response data
     * @return The created response
     */
    ResponseDto createResponse(ResponseDto responseDto);
    
    /**
     * Update an existing response.
     * 
     * @param id The response ID
     * @param responseDto The updated response data
     * @return The updated response
     */
    ResponseDto updateResponse(String id, ResponseDto responseDto);
    
    /**
     * Get a response by ID.
     * 
     * @param id The response ID
     * @return The response, if found
     */
    Optional<ResponseDto> getResponseById(String id);
    
    /**
     * Delete a response by ID.
     * 
     * @param id The response ID
     */
    void deleteResponse(String id);
    
    /**
     * Get all responses for a form.
     * 
     * @param formId The form ID
     * @param pageable Pagination information
     * @return Page of responses
     */
    Page<ResponseDto> getResponsesByFormId(Long formId, Pageable pageable);
    
    /**
     * Get all responses for a form with a specific status.
     * 
     * @param formId The form ID
     * @param status The response status
     * @param pageable Pagination information
     * @return Page of responses
     */
    Page<ResponseDto> getResponsesByFormIdAndStatus(Long formId, String status, Pageable pageable);
    
    /**
     * Get all responses submitted by a user.
     * 
     * @param userId The user ID
     * @param pageable Pagination information
     * @return Page of responses
     */
    Page<ResponseDto> getResponsesByUserId(Long userId, Pageable pageable);
    
    /**
     * Get all responses for a form submitted by a user.
     * 
     * @param formId The form ID
     * @param userId The user ID
     * @return List of responses
     */
    List<ResponseDto> getResponsesByFormIdAndUserId(Long formId, Long userId);
    
    /**
     * Get all responses submitted within a date range.
     * 
     * @param formId The form ID
     * @param startDate The start date
     * @param endDate The end date
     * @param pageable Pagination information
     * @return Page of responses
     */
    Page<ResponseDto> getResponsesByDateRange(Long formId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Search for responses with specific answer data.
     * 
     * @param formId The form ID
     * @param questionId The question ID
     * @param answer The answer to search for
     * @param pageable Pagination information
     * @return Page of responses
     */
    Page<ResponseDto> searchResponsesByAnswer(Long formId, String questionId, Object answer, Pageable pageable);
    
    /**
     * Save a draft response.
     * 
     * @param responseDto The draft response data
     * @return The saved draft response
     */
    ResponseDto saveDraftResponse(ResponseDto responseDto);
    
    /**
     * Get the latest draft response for a form and user.
     * 
     * @param formId The form ID
     * @param userId The user ID
     * @return The latest draft response, if any
     */
    Optional<ResponseDto> getLatestDraftResponse(Long formId, Long userId);
    
    /**
     * Submit a response (convert from draft to submitted).
     * 
     * @param id The response ID
     * @return The submitted response
     */
    ResponseDto submitResponse(String id);
    
    /**
     * Export responses for a form to CSV format.
     * 
     * @param formId The form ID
     * @return CSV data as a string
     */
    String exportResponsesToCsv(Long formId);
    
    /**
     * Import responses from CSV data.
     * 
     * @param formId The form ID
     * @param csvData The CSV data
     * @return The number of imported responses
     */
    int importResponsesFromCsv(Long formId, String csvData);
    
    /**
     * Generate test responses for a form.
     * 
     * @param formId The form ID
     * @param count The number of responses to generate
     * @return The generated responses
     */
    List<ResponseDto> generateTestResponses(Long formId, int count);
    
    /**
     * Get response statistics for a form.
     * 
     * @param formId The form ID
     * @return Map of statistics
     */
    Map<String, Object> getResponseStatistics(Long formId);
    
    /**
     * Get time-series response data for a form.
     * 
     * @param formId The form ID
     * @param startDate The start date
     * @param endDate The end date
     * @param interval The interval (e.g., "day", "week", "month")
     * @return Time-series data
     */
    List<Map<String, Object>> getResponseTimeSeries(Long formId, LocalDateTime startDate, LocalDateTime endDate, String interval);
}