package org.microservices.responseservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * MongoDB document for storing form responses.
 */
@Document(collection = "responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    /**
     * The response ID.
     */
    @Id
    private String id;

    /**
     * The ID of the form this response is for.
     * Indexed for faster queries.
     */
    @Indexed
    private Long formId;

    /**
     * The ID of the user who submitted the response.
     * Null for anonymous responses.
     * Indexed for faster queries.
     */
    @Indexed
    private Long userId;

    /**
     * The response data, mapping question IDs to answers.
     */
    private Map<String, Object> responseData;

    /**
     * The status of the response (DRAFT, SUBMITTED).
     * Indexed for faster queries.
     */
    @Indexed
    private String status;

    /**
     * The submission timestamp.
     * Indexed for faster queries.
     */
    @Indexed
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
}