package org.microservices.responseservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microservices.responseservice.dto.ResponseDto;
import org.microservices.responseservice.model.Response;
import org.microservices.responseservice.repository.ResponseRepository;
import org.microservices.responseservice.service.ResponseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.microservices.responseservice.mappers.ResponseMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the ResponseService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResponseServiceImpl implements ResponseService {

    private final ResponseRepository responseRepository;
    private final MongoTemplate mongoTemplate;
    private final ValidationService validationService;
    private final ResponseMapper responseMapper;

    /**
     * Create and persist a new response after validating its contents.
     * @param responseDto response payload to create
     * @return created response as DTO
     */
    @Override
    public ResponseDto createResponse(ResponseDto responseDto) {
        log.info("Creating response for form ID: {}", responseDto.getFormId());
        validationService.validateResponse(responseDto);

        Response response = responseMapper.toEntity(responseDto);
        response.prePersist();
        Response savedResponse = responseRepository.save(response);

        return responseMapper.toDto(savedResponse);
    }

    /**
     * Update an existing response by ID. Preserves createdAt of the existing entity.
     * @param id response identifier
     * @param responseDto new data
     * @return updated response as DTO
     */
    @Override
    public ResponseDto updateResponse(String id, ResponseDto responseDto) {
        log.info("Updating response with ID: {}", id);
        return responseRepository.findById(id)
                .map(existingResponse -> {
                    Response response = responseMapper.toEntity(responseDto);
                    response.setId(id);
                    response.setCreatedAt(existingResponse.getCreatedAt());
                    response.prePersist();
                    Response updatedResponse = responseRepository.save(response);

                    return responseMapper.toDto(updatedResponse);
                })
                .orElseThrow(() -> new java.util.NoSuchElementException("Response not found with ID: " + id));
    }

    /**
     * Retrieve a response by its ID.
     * @param id response identifier
     * @return optional DTO if found
     */
    @Override
    public Optional<ResponseDto> getResponseById(String id) {
        log.info("Getting response with ID: {}", id);
        return responseRepository.findById(id).map(responseMapper::toDto);
    }

    /**
     * Delete a response by its ID.
     * @param id response identifier
     */
    @Override
    public void deleteResponse(String id) {
        log.info("Deleting response with ID: {}", id);
        responseRepository.deleteById(id);
    }

    /**
     * Page responses by form identifier.
     * @param formId form identifier
     * @param pageable pagination
     * @return page of response DTOs
     */
    @Override
    public Page<ResponseDto> getResponsesByFormId(Long formId, Pageable pageable) {
        log.info("Getting responses for form ID: {}", formId);
        return responseRepository.findByFormId(formId, pageable).map(responseMapper::toDto);
    }

    /**
     * Page responses by form and status.
     * @param formId form identifier
     * @param status response status filter
     * @param pageable pagination
     * @return page of response DTOs
     */
    @Override
    public Page<ResponseDto> getResponsesByFormIdAndStatus(Long formId, String status, Pageable pageable) {
        log.info("Getting responses for form ID: {} with status: {}", formId, status);
        return responseRepository.findByFormIdAndStatus(formId, status, pageable).map(responseMapper::toDto);
    }

    /**
     * Page responses by submitting user.
     * @param userId user identifier
     * @param pageable pagination
     * @return page of response DTOs
     */
    @Override
    public Page<ResponseDto> getResponsesByUserId(Long userId, Pageable pageable) {
        log.info("Getting responses for user ID: {}", userId);
        return responseRepository.findByUserId(userId, pageable).map(responseMapper::toDto);
    }

    /**
     * List responses for a form submitted by a given user.
     * @param formId form identifier
     * @param userId user identifier
     * @return list of response DTOs
     */
    @Override
    public List<ResponseDto> getResponsesByFormIdAndUserId(Long formId, Long userId) {
        log.info("Getting responses for form ID: {} and user ID: {}", formId, userId);
        return responseRepository.findByFormIdAndUserId(formId, userId).stream()
                .map(responseMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Page responses by form within a submittedAt date range.
     * @param formId form identifier
     * @param startDate inclusive start of range
     * @param endDate inclusive end of range
     * @param pageable pagination
     * @return page of response DTOs
     */
    @Override
    public Page<ResponseDto> getResponsesByDateRange(Long formId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.info("Getting responses for form ID: {} between {} and {}", formId, startDate, endDate);
        return responseRepository.findByFormIdAndSubmittedAtBetween(formId, startDate, endDate, pageable)
                .map(responseMapper::toDto);
    }

    /**
     * Search responses by a specific question's answer value.
     * @param formId form identifier
     * @param questionId question ID inside the response data
     * @param answer value to match
     * @param pageable pagination
     * @return page of response DTOs matching search criteria
     */
    @Override
    public Page<ResponseDto> searchResponsesByAnswer(Long formId, String questionId, Object answer, Pageable pageable) {
        log.info("Searching responses for form ID: {} with question ID: {} and answer: {}", formId, questionId, answer);
        return responseRepository.findByFormIdAndQuestionAnswer(formId, questionId, answer, pageable)
                .map(responseMapper::toDto);
    }

    /**
     * Save a response as a draft (does not require full validation of answers).
     * @param responseDto response payload to store as draft
     * @return saved draft as DTO
     */
    @Override
    public ResponseDto saveDraftResponse(ResponseDto responseDto) {
        log.info("Saving draft response for form ID: {} and user ID: {}", responseDto.getFormId(), responseDto.getUserId());
        responseDto.setStatus("DRAFT");
        Response response = responseMapper.toEntity(responseDto);
        response.prePersist();
        Response savedResponse = responseRepository.save(response);
        return responseMapper.toDto(savedResponse);
    }

    /**
     * Fetch the most recently updated draft response for a form and user.
     * @param formId form identifier
     * @param userId user identifier
     * @return optional draft response DTO
     */
    @Override
    public Optional<ResponseDto> getLatestDraftResponse(Long formId, Long userId) {
        log.info("Getting latest draft response for form ID: {} and user ID: {}", formId, userId);
        return responseRepository.findFirstByFormIdAndUserIdAndStatusOrderByUpdatedAtDesc(formId, userId, "DRAFT")
                .map(responseMapper::toDto);
    }

    /**
     * Mark a draft response as SUBMITTED and set submittedAt.
     * @param id response identifier
     * @return submitted response DTO
     */
    @Override
    public ResponseDto submitResponse(String id) {
        log.info("Submitting response with ID: {}", id);
        return responseRepository.findById(id)
                .map(response -> {
                    response.setStatus("SUBMITTED");
                    response.setSubmittedAt(LocalDateTime.now());
                    response.prePersist();
                    Response submittedResponse = responseRepository.save(response);

                    return responseMapper.toDto(submittedResponse);
                })
                .orElseThrow(() -> new java.util.NoSuchElementException("Response not found with ID: " + id));
    }

    /**
     * Export all responses for a form into a simple CSV string.
     * Note: This is a basic exporter intended for analytics and backups.
     * @param formId form identifier
     * @return CSV content
     */
    @Override
    public String exportResponsesToCsv(Long formId) {
        log.info("Exporting responses for form ID: {} to CSV", formId);
        List<Response> responses = responseRepository.findByFormId(formId);

        StringBuilder csv = new StringBuilder();

        csv.append("ID,Form ID,User ID,Status,Submitted At,Created At,Updated At,IP Address,User Agent\n");

        for (Response response : responses) {
            csv.append(response.getId()).append(",")
               .append(response.getFormId()).append(",")
               .append(response.getUserId() != null ? response.getUserId() : "").append(",")
               .append(response.getStatus()).append(",")
               .append(response.getSubmittedAt() != null ? response.getSubmittedAt() : "").append(",")
               .append(response.getCreatedAt()).append(",")
               .append(response.getUpdatedAt()).append(",")
               .append(response.getIpAddress() != null ? response.getIpAddress() : "").append(",")
               .append(response.getUserAgent() != null ? response.getUserAgent() : "").append("\n");
        }

        return csv.toString();
    }

    /**
     * Import responses from a CSV string. Assumes header row and fixed column order.
     * @param formId form identifier to associate imported responses with
     * @param csvData CSV contents
     * @return number of created records
     */
    @Override
    public int importResponsesFromCsv(Long formId, String csvData) {
        log.info("Importing responses for form ID: {} from CSV", formId);

        String[] lines = csvData.split("\n");
        if (lines.length <= 1) {
            return 0;
        }

        int importedCount = 0;
        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].split(",");
            if (fields.length >= 9) {
                Response response = Response.builder()
                        .formId(formId)
                        .userId(fields[2].isEmpty() ? null : Long.parseLong(fields[2]))
                        .status(fields[3])
                        .submittedAt(fields[4].isEmpty() ? null : LocalDateTime.parse(fields[4]))
                        .createdAt(LocalDateTime.parse(fields[5]))
                        .updatedAt(LocalDateTime.parse(fields[6]))
                        .ipAddress(fields[7].isEmpty() ? null : fields[7])
                        .userAgent(fields[8].isEmpty() ? null : fields[8])
                        .build();

                responseRepository.save(response);
                importedCount++;
            }
        }

        return importedCount;
    }

    /**
     * Compute basic statistics for responses of a form, such as totals and average submission time.
     * @param formId form identifier
     * @return map of statistics
     */
    @Override
    public Map<String, Object> getResponseStatistics(Long formId) {
        log.info("Getting response statistics for form ID: {}", formId);

        List<Response> responses = responseRepository.findByFormId(formId);

        Map<String, Object> statistics = new HashMap<>();

        statistics.put("totalResponses", responses.size());

        Map<String, Long> responsesByStatus = responses.stream()
                .collect(Collectors.groupingBy(Response::getStatus, Collectors.counting()));
        statistics.put("responsesByStatus", responsesByStatus);

        OptionalDouble avgResponseTime = responses.stream()
                .filter(r -> r.getSubmittedAt() != null && r.getCreatedAt() != null)
                .mapToLong(r -> ChronoUnit.SECONDS.between(r.getCreatedAt(), r.getSubmittedAt()))
                .average();
        statistics.put("averageResponseTimeSeconds", avgResponseTime.orElse(0));

        Map<LocalDateTime, Long> responsesPerDay = responses.stream()
                .filter(r -> r.getSubmittedAt() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getSubmittedAt().truncatedTo(ChronoUnit.DAYS),
                        Collectors.counting()));
        statistics.put("responsesPerDay", responsesPerDay);

        return statistics;
    }

    /**
     * Build a time series of submitted responses grouped by day/week/month.
     * @param formId form identifier
     * @param startDate start of time window (inclusive)
     * @param endDate end of time window (inclusive)
     * @param interval grouping interval: hour, day, week or month
     * @return list of entries with date and count
     */
    @Override
    public List<Map<String, Object>> getResponseTimeSeries(Long formId, LocalDateTime startDate, LocalDateTime endDate, String interval) {
        log.info("Getting response time series for form ID: {} between {} and {} with interval: {}", 
                formId, startDate, endDate, interval);

        // Define the time unit based on the interval
        ChronoUnit timeUnit = switch (interval.toLowerCase()) {
            case "hour" -> ChronoUnit.HOURS;
            case "week" -> ChronoUnit.WEEKS;
            case "month" -> ChronoUnit.MONTHS;
            default -> ChronoUnit.DAYS;
        };

        // Use MongoDB aggregation to get time series data
        Criteria criteria = Criteria.where("formId").is(formId)
                .and("submittedAt").gte(startDate).lte(endDate);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.project()
                        .and("submittedAt").extractYear().as("year")
                        .and("submittedAt").extractMonth().as("month")
                        .and("submittedAt").extractDayOfMonth().as("day"),
                Aggregation.group("year", "month", "day").count().as("count"),
                Aggregation.project("count").and("_id").as("date")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(
                aggregation, "responses", Map.class);

        return results.getMappedResults().stream()
                .map(result -> {
                    Map<String, Object> entry = new HashMap<>();
                    Map<String, Object> dateMap = (Map<String, Object>) result.get("date");
                    int year = (int) dateMap.get("year");
                    int month = (int) dateMap.get("month");
                    int day = (int) dateMap.get("day");

                    LocalDateTime date = LocalDateTime.of(year, month, day, 0, 0);
                    entry.put("date", date);
                    entry.put("count", result.get("count"));
                    return entry;
                })
                .collect(Collectors.toList());
    }

}