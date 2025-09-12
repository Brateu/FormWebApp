package org.microservices.responseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for Form Response.
 * Used for transferring form response data between services and to clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    
    /**
     * The response ID.
     */
    private String id;
    
    /**
     * The ID of the form this response is for.
     */
    private Long formId;
    
    /**
     * The ID of the user who submitted the response.
     * Null for anonymous responses.
     */
    private Long userId;
    
    /**
     * The response answers as a polymorphic list of answered questions.
     */
    private List<AnsweredQuestionDto> answeredQuestions;

    /**
     * The question definitions used for validation (offline, no FormStructure).
     */
    private List<QuestionDefinitionDto> questionDefinitions;
    
    /**
     * The status of the response (DRAFT, SUBMITTED).
     */
    private String status;
    
    /**
     * The submission timestamp.
     */
    private LocalDateTime submittedAt;
    
    /**
     * The creation timestamp.
     */
    private LocalDateTime createdAt;
    
    /**
     * The last update timestamp.
     */
    private LocalDateTime updatedAt;
    
    /**
     * IP address of the submitter.
     */
    private String ipAddress;
    
    /**
     * User agent of the submitter.
     */
    private String userAgent;
    
    /**
     * Metadata for the response.
     */
    private Map<String, Object> metadata;
}