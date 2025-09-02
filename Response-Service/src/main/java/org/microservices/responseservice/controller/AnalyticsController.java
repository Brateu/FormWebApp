package org.microservices.responseservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microservices.responseservice.service.ResponseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for response analytics.
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final ResponseService responseService;

    /**
     * Get response statistics for a form.
     *
     * @param formId The form ID
     * @return Map of statistics
     */
    @GetMapping("/statistics/{formId}")
    public ResponseEntity<Map<String, Object>> getResponseStatistics(@PathVariable Long formId) {
        log.info("REST request to get response statistics for form ID: {}", formId);
        Map<String, Object> statistics = responseService.getResponseStatistics(formId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get time-series response data for a form.
     *
     * @param formId The form ID
     * @param startDate The start date
     * @param endDate The end date
     * @param interval The interval (e.g., "hour", "day", "week", "month")
     * @return Time-series data
     */
    @GetMapping("/time-series/{formId}")
    public ResponseEntity<List<Map<String, Object>>> getResponseTimeSeries(
            @PathVariable Long formId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "day") String interval) {
        
        log.info("REST request to get response time series for form ID: {} between {} and {} with interval: {}", 
                formId, startDate, endDate, interval);
        
        List<Map<String, Object>> timeSeriesData = responseService.getResponseTimeSeries(
                formId, startDate, endDate, interval);
        
        return ResponseEntity.ok(timeSeriesData);
    }

    /**
     * Get response completion rate for a form.
     *
     * @param formId The form ID
     * @return Completion rate data
     */
    @GetMapping("/completion-rate/{formId}")
    public ResponseEntity<Map<String, Object>> getCompletionRate(@PathVariable Long formId) {
        log.info("REST request to get completion rate for form ID: {}", formId);
        
        // Calculate completion rate from statistics
        Map<String, Object> statistics = responseService.getResponseStatistics(formId);
        
        Map<String, Long> responsesByStatus = (Map<String, Long>) statistics.get("responsesByStatus");
        long totalResponses = (long) statistics.get("totalResponses");
        long submittedResponses = responsesByStatus.getOrDefault("SUBMITTED", 0L);
        long draftResponses = responsesByStatus.getOrDefault("DRAFT", 0L);
        
        double completionRate = totalResponses > 0 ? 
                (double) submittedResponses / totalResponses * 100 : 0;
        
        Map<String, Object> completionData = Map.of(
                "totalResponses", totalResponses,
                "submittedResponses", submittedResponses,
                "draftResponses", draftResponses,
                "completionRate", completionRate
        );
        
        return ResponseEntity.ok(completionData);
    }

    /**
     * Get response distribution by question for a form.
     *
     * @param formId The form ID
     * @param questionId The question ID
     * @return Distribution data
     */
    @GetMapping("/distribution/{formId}")
    public ResponseEntity<Map<String, Object>> getResponseDistribution(
            @PathVariable Long formId,
            @RequestParam String questionId) {
        
        log.info("REST request to get response distribution for form ID: {} and question ID: {}", 
                formId, questionId);
        
        // This would typically involve a custom aggregation query to count responses by answer
        // For simplicity, we'll return a placeholder implementation
        
        Map<String, Object> distributionData = Map.of(
                "formId", formId,
                "questionId", questionId,
                "distribution", Map.of(
                        "Option A", 25,
                        "Option B", 35,
                        "Option C", 20,
                        "Option D", 20
                )
        );
        
        return ResponseEntity.ok(distributionData);
    }

    /**
     * Get average response time for a form.
     *
     * @param formId The form ID
     * @return Average response time data
     */
    @GetMapping("/response-time/{formId}")
    public ResponseEntity<Map<String, Object>> getAverageResponseTime(@PathVariable Long formId) {
        log.info("REST request to get average response time for form ID: {}", formId);
        
        // Get from statistics
        Map<String, Object> statistics = responseService.getResponseStatistics(formId);
        double avgResponseTimeSeconds = (double) statistics.get("averageResponseTimeSeconds");
        
        Map<String, Object> responseTimeData = Map.of(
                "formId", formId,
                "averageResponseTimeSeconds", avgResponseTimeSeconds,
                "averageResponseTimeMinutes", avgResponseTimeSeconds / 60
        );
        
        return ResponseEntity.ok(responseTimeData);
    }
}