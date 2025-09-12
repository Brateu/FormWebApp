package org.microservices.responseservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microservices.responseservice.dto.ResponseDto;
import org.microservices.responseservice.service.ResponseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing form responses.
 */
@RestController
@RequestMapping("/api/responses")
@RequiredArgsConstructor
@Slf4j
public class ResponseController {

    private final ResponseService responseService;

    /**
     * Create a new response.
     *
     * @param responseDto The response data
     * @return The created response
     */
    @PostMapping
    public ResponseEntity<ResponseDto> createResponse(@RequestHeader("X-User-ID") Long userId,
            @RequestBody ResponseDto responseDto) {
        log.info("REST request to create response for form ID: {}", responseDto.getFormId());
        responseDto.setUserId(userId);
        ResponseDto createdResponse = responseService.createResponse(responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResponse);
    }

    /**
     * Update an existing response.
     *
     * @param id The response ID
     * @param responseDto The updated response data
     * @return The updated response
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateResponse(@PathVariable String id, @RequestBody ResponseDto responseDto) {
        log.info("REST request to update response with ID: {}", id);
        ResponseDto updatedResponse = responseService.updateResponse(id, responseDto);
        return ResponseEntity.ok(updatedResponse);
    }

    /**
     * Get a response by ID.
     *
     * @param id The response ID
     * @return The response, if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getResponseById(@PathVariable String id) {
        log.info("REST request to get response with ID: {}", id);
        Optional<ResponseDto> response = responseService.getResponseById(id);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a response by ID.
     *
     * @param id The response ID
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResponse(@PathVariable String id) {
        log.info("REST request to delete response with ID: {}", id);
        responseService.deleteResponse(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all responses for a form with pagination and filtering.
     *
     * @param formId The form ID
     * @param status Optional status filter
     * @param startDate Optional start date filter
     * @param endDate Optional end date filter
     * @param page Page number
     * @param size Page size
     * @param sort Sort field
     * @param direction Sort direction
     * @return Page of responses
     */
    @GetMapping("/form/{formId}")
    public ResponseEntity<Page<ResponseDto>> getResponsesByFormId(
            @PathVariable Long formId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "submittedAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        log.info("REST request to get responses for form ID: {} with filters", formId);
        
        Pageable pageable = PageRequest.of(page, size, 
                Sort.Direction.fromString(direction), sort);
        
        Page<ResponseDto> responses;
        
        if (status != null && startDate != null && endDate != null) {
            // Filter by status and date range
            responses = responseService.getResponsesByFormIdAndStatus(formId, status, pageable);
            // Note: This is a simplification. In a real implementation, we would need a more complex query
            // that combines both status and date range filters.
        } else if (status != null) {
            // Filter by status
            responses = responseService.getResponsesByFormIdAndStatus(formId, status, pageable);
        } else if (startDate != null && endDate != null) {
            // Filter by date range
            responses = responseService.getResponsesByDateRange(formId, startDate, endDate, pageable);
        } else {
            // No filters
            responses = responseService.getResponsesByFormId(formId, pageable);
        }
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all responses submitted by a user.
     *
     * @param userId The user ID
     * @param page Page number
     * @param size Page size
     * @return Page of responses
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ResponseDto>> getResponsesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("REST request to get responses for user ID: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ResponseDto> responses = responseService.getResponsesByUserId(userId, pageable);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Search for responses with specific answer data.
     *
     * @param formId The form ID
     * @param questionId The question ID
     * @param answer The answer to search for
     * @param page Page number
     * @param size Page size
     * @return Page of responses
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ResponseDto>> searchResponses(
            @RequestParam Long formId,
            @RequestParam String questionId,
            @RequestParam String answer,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("REST request to search responses for form ID: {} with question ID: {} and answer: {}", 
                formId, questionId, answer);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ResponseDto> responses = responseService.searchResponsesByAnswer(formId, questionId, answer, pageable);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Save a draft response.
     *
     * @param responseDto The draft response data
     * @return The saved draft response
     */
    @PostMapping("/draft")
    public ResponseEntity<ResponseDto> saveDraftResponse(@RequestBody ResponseDto responseDto) {
        log.info("REST request to save draft response for form ID: {}", responseDto.getFormId());
        ResponseDto draftResponse = responseService.saveDraftResponse(responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(draftResponse);
    }

    /**
     * Get the latest draft response for a form and user.
     *
     * @param formId The form ID
     * @param userId The user ID
     * @return The latest draft response, if any
     */
    @GetMapping("/draft")
    public ResponseEntity<ResponseDto> getLatestDraftResponse(
            @RequestParam Long formId,
            @RequestParam Long userId) {
        
        log.info("REST request to get latest draft response for form ID: {} and user ID: {}", formId, userId);
        
        Optional<ResponseDto> draftResponse = responseService.getLatestDraftResponse(formId, userId);
        
        return draftResponse.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Submit a response (convert from draft to submitted).
     *
     * @param id The response ID
     * @return The submitted response
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<ResponseDto> submitResponse(@PathVariable String id) {
        log.info("REST request to submit response with ID: {}", id);
        ResponseDto submittedResponse = responseService.submitResponse(id);
        return ResponseEntity.ok(submittedResponse);
    }

    /**
     * Export responses for a form to CSV format.
     *
     * @param formId The form ID
     * @return CSV data as a string
     */
    @GetMapping("/export/csv")
    public ResponseEntity<String> exportResponsesToCsv(@RequestParam Long formId) {
        log.info("REST request to export responses for form ID: {} to CSV", formId);
        String csvData = responseService.exportResponsesToCsv(formId);
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=\"responses_" + formId + ".csv\"")
                .body(csvData);
    }

    /**
     * Import responses from CSV data.
     *
     * @param formId The form ID
     * @param csvData The CSV data
     * @return The number of imported responses
     */
    @PostMapping("/import/csv")
    public ResponseEntity<Map<String, Integer>> importResponsesFromCsv(
            @RequestParam Long formId,
            @RequestBody String csvData) {
        
        log.info("REST request to import responses for form ID: {} from CSV", formId);
        
        int importedCount = responseService.importResponsesFromCsv(formId, csvData);
        
        return ResponseEntity.ok(Map.of("importedCount", importedCount));
    }

}