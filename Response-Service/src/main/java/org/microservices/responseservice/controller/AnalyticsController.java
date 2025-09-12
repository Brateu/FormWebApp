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
        
        Map<String, Object> statistics = getStatisticsForForm(formId);
        Map<String, Object> completionData = buildCompletionData(statistics);
        
        return ResponseEntity.ok(completionData);
    }

    /**
     * Internal helpers to keep analytics controller lean.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> buildCompletionData(Map<String, Object> statistics) {
        Map<String, Long> responsesByStatus = (Map<String, Long>) statistics.getOrDefault("responsesByStatus", Map.of());
        long totalResponses = ((Number) statistics.getOrDefault("totalResponses", 0L)).longValue();
        long submittedResponses = responsesByStatus.getOrDefault("SUBMITTED", 0L);
        long draftResponses = responsesByStatus.getOrDefault("DRAFT", 0L);

        double completionRate = totalResponses > 0
                ? (double) submittedResponses / totalResponses * 100
                : 0;

        return Map.of(
                "totalResponses", totalResponses,
                "submittedResponses", submittedResponses,
                "draftResponses", draftResponses,
                "completionRate", completionRate
        );
    }

    private Map<String, Object> getStatisticsForForm(Long formId) {
        log.debug("Fetching response statistics for form ID: {}", formId);
        return responseService.getResponseStatistics(formId);
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
        
        Map<String, Object> statistics = getStatisticsForForm(formId);
        double avgResponseTimeSeconds = (double) statistics.get("averageResponseTimeSeconds");
        
        Map<String, Object> responseTimeData = Map.of(
                "formId", formId,
                "averageResponseTimeSeconds", avgResponseTimeSeconds,
                "averageResponseTimeMinutes", avgResponseTimeSeconds / 60
        );
        
        return ResponseEntity.ok(responseTimeData);
    }
}