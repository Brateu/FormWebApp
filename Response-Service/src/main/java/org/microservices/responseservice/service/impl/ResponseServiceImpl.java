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

    @Override
    public ResponseDto createResponse(ResponseDto responseDto) {
        log.info("Creating response for form ID: {}", responseDto.getFormId());
        Response response = mapToEntity(responseDto);
        response.prePersist();
        Response savedResponse = responseRepository.save(response);
        ResponseDto savedResponseDto = mapToDto(savedResponse);

        return savedResponseDto;
    }

    @Override
    public ResponseDto updateResponse(String id, ResponseDto responseDto) {
        log.info("Updating response with ID: {}", id);
        return responseRepository.findById(id)
                .map(existingResponse -> {
                    Response response = mapToEntity(responseDto);
                    response.setId(id);
                    response.setCreatedAt(existingResponse.getCreatedAt());
                    response.prePersist();
                    Response updatedResponse = responseRepository.save(response);
                    ResponseDto updatedResponseDto = mapToDto(updatedResponse);

                    return updatedResponseDto;
                })
                .orElseThrow(() -> new RuntimeException("Response not found with ID: " + id));
    }

    @Override
    public Optional<ResponseDto> getResponseById(String id) {
        log.info("Getting response with ID: {}", id);
        return responseRepository.findById(id).map(this::mapToDto);
    }

    @Override
    public void deleteResponse(String id) {
        log.info("Deleting response with ID: {}", id);
        responseRepository.deleteById(id);
    }

    @Override
    public Page<ResponseDto> getResponsesByFormId(Long formId, Pageable pageable) {
        log.info("Getting responses for form ID: {}", formId);
        return responseRepository.findByFormId(formId, pageable).map(this::mapToDto);
    }

    @Override
    public Page<ResponseDto> getResponsesByFormIdAndStatus(Long formId, String status, Pageable pageable) {
        log.info("Getting responses for form ID: {} with status: {}", formId, status);
        return responseRepository.findByFormIdAndStatus(formId, status, pageable).map(this::mapToDto);
    }

    @Override
    public Page<ResponseDto> getResponsesByUserId(Long userId, Pageable pageable) {
        log.info("Getting responses for user ID: {}", userId);
        return responseRepository.findByUserId(userId, pageable).map(this::mapToDto);
    }

    @Override
    public List<ResponseDto> getResponsesByFormIdAndUserId(Long formId, Long userId) {
        log.info("Getting responses for form ID: {} and user ID: {}", formId, userId);
        return responseRepository.findByFormIdAndUserId(formId, userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ResponseDto> getResponsesByDateRange(Long formId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.info("Getting responses for form ID: {} between {} and {}", formId, startDate, endDate);
        return responseRepository.findByFormIdAndSubmittedAtBetween(formId, startDate, endDate, pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<ResponseDto> searchResponsesByAnswer(Long formId, String questionId, Object answer, Pageable pageable) {
        log.info("Searching responses for form ID: {} with question ID: {} and answer: {}", formId, questionId, answer);
        return responseRepository.findByFormIdAndQuestionAnswer(formId, questionId, answer, pageable)
                .map(this::mapToDto);
    }

    @Override
    public ResponseDto saveDraftResponse(ResponseDto responseDto) {
        log.info("Saving draft response for form ID: {} and user ID: {}", responseDto.getFormId(), responseDto.getUserId());
        responseDto.setStatus("DRAFT");
        Response response = mapToEntity(responseDto);
        response.prePersist();
        Response savedResponse = responseRepository.save(response);
        return mapToDto(savedResponse);
    }

    @Override
    public Optional<ResponseDto> getLatestDraftResponse(Long formId, Long userId) {
        log.info("Getting latest draft response for form ID: {} and user ID: {}", formId, userId);
        return responseRepository.findFirstByFormIdAndUserIdAndStatusOrderByUpdatedAtDesc(formId, userId, "DRAFT")
                .map(this::mapToDto);
    }

    @Override
    public ResponseDto submitResponse(String id) {
        log.info("Submitting response with ID: {}", id);
        return responseRepository.findById(id)
                .map(response -> {
                    response.setStatus("SUBMITTED");
                    response.setSubmittedAt(LocalDateTime.now());
                    response.prePersist();
                    Response submittedResponse = responseRepository.save(response);
                    ResponseDto submittedResponseDto = mapToDto(submittedResponse);

                    return submittedResponseDto;
                })
                .orElseThrow(() -> new RuntimeException("Response not found with ID: " + id));
    }

    @Override
    public String exportResponsesToCsv(Long formId) {
        log.info("Exporting responses for form ID: {} to CSV", formId);
        List<Response> responses = responseRepository.findByFormId(formId);

        // Simple CSV export implementation
        StringBuilder csv = new StringBuilder();

        // Add header row
        csv.append("ID,Form ID,User ID,Status,Submitted At,Created At,Updated At,IP Address,User Agent\n");

        // Add data rows
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

    @Override
    public int importResponsesFromCsv(Long formId, String csvData) {
        log.info("Importing responses for form ID: {} from CSV", formId);

        // Simple CSV import implementation
        String[] lines = csvData.split("\n");
        if (lines.length <= 1) {
            return 0; // Only header or empty file
        }

        int importedCount = 0;
        for (int i = 1; i < lines.length; i++) { // Skip header
            String[] fields = lines[i].split(",");
            if (fields.length >= 9) {
                Response response = new Response();
                response.setFormId(formId);
                response.setUserId(fields[2].isEmpty() ? null : Long.parseLong(fields[2]));
                response.setStatus(fields[3]);
                response.setSubmittedAt(fields[4].isEmpty() ? null : LocalDateTime.parse(fields[4]));
                response.setCreatedAt(LocalDateTime.parse(fields[5]));
                response.setUpdatedAt(LocalDateTime.parse(fields[6]));
                response.setIpAddress(fields[7].isEmpty() ? null : fields[7]);
                response.setUserAgent(fields[8].isEmpty() ? null : fields[8]);

                responseRepository.save(response);
                importedCount++;
            }
        }

        return importedCount;
    }

    @Override
    public List<ResponseDto> generateTestResponses(Long formId, int count) {
        log.info("Generating {} test responses for form ID: {}", count, formId);

        List<Response> testResponses = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            Response response = new Response();
            response.setFormId(formId);
            response.setUserId(random.nextLong(1000) + 1); // Random user ID between 1 and 1000
            response.setStatus("SUBMITTED");

            // Generate random dates within the last 30 days
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime randomDate = now.minusDays(random.nextInt(30));
            response.setSubmittedAt(randomDate);
            response.setCreatedAt(randomDate.minusMinutes(random.nextInt(60)));
            response.setUpdatedAt(randomDate);

            response.setIpAddress("192.168.1." + random.nextInt(255));
            response.setUserAgent("Mozilla/5.0 Test User Agent");

            // Generate random response data
            Map<String, Object> responseData = new HashMap<>();
            for (int j = 1; j <= 5; j++) {
                responseData.put("question_" + j, "Answer " + random.nextInt(10));
            }
            response.setResponseData(responseData);

            testResponses.add(response);
        }

        List<Response> savedResponses = responseRepository.saveAll(testResponses);
        return savedResponses.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getResponseStatistics(Long formId) {
        log.info("Getting response statistics for form ID: {}", formId);

        List<Response> responses = responseRepository.findByFormId(formId);

        Map<String, Object> statistics = new HashMap<>();

        // Total responses
        statistics.put("totalResponses", responses.size());

        // Responses by status
        Map<String, Long> responsesByStatus = responses.stream()
                .collect(Collectors.groupingBy(Response::getStatus, Collectors.counting()));
        statistics.put("responsesByStatus", responsesByStatus);

        // Average response time (time between created and submitted)
        OptionalDouble avgResponseTime = responses.stream()
                .filter(r -> r.getSubmittedAt() != null && r.getCreatedAt() != null)
                .mapToLong(r -> ChronoUnit.SECONDS.between(r.getCreatedAt(), r.getSubmittedAt()))
                .average();
        statistics.put("averageResponseTimeSeconds", avgResponseTime.orElse(0));

        // Responses per day
        Map<LocalDateTime, Long> responsesPerDay = responses.stream()
                .filter(r -> r.getSubmittedAt() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getSubmittedAt().truncatedTo(ChronoUnit.DAYS),
                        Collectors.counting()));
        statistics.put("responsesPerDay", responsesPerDay);

        return statistics;
    }

    @Override
    public List<Map<String, Object>> getResponseTimeSeries(Long formId, LocalDateTime startDate, LocalDateTime endDate, String interval) {
        log.info("Getting response time series for form ID: {} between {} and {} with interval: {}", 
                formId, startDate, endDate, interval);

        // Define the time unit based on the interval
        ChronoUnit timeUnit;
        switch (interval.toLowerCase()) {
            case "hour":
                timeUnit = ChronoUnit.HOURS;
                break;
            case "day":
                timeUnit = ChronoUnit.DAYS;
                break;
            case "week":
                timeUnit = ChronoUnit.WEEKS;
                break;
            case "month":
                timeUnit = ChronoUnit.MONTHS;
                break;
            default:
                timeUnit = ChronoUnit.DAYS;
        }

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

    /**
     * Maps a Response entity to a ResponseDto.
     * 
     * @param response The Response entity
     * @return The ResponseDto
     */
    private ResponseDto mapToDto(Response response) {
        ResponseDto dto = new ResponseDto();
        dto.setId(response.getId());
        dto.setFormId(response.getFormId());
        dto.setUserId(response.getUserId());
        dto.setResponseData(response.getResponseData());
        dto.setStatus(response.getStatus());
        dto.setSubmittedAt(response.getSubmittedAt());
        dto.setCreatedAt(response.getCreatedAt());
        dto.setUpdatedAt(response.getUpdatedAt());
        dto.setIpAddress(response.getIpAddress());
        dto.setUserAgent(response.getUserAgent());
        dto.setMetadata(response.getMetadata());
        return dto;
    }

    /**
     * Maps a ResponseDto to a Response entity.
     * 
     * @param dto The ResponseDto
     * @return The Response entity
     */
    private Response mapToEntity(ResponseDto dto) {
        Response response = new Response();
        response.setId(dto.getId());
        response.setFormId(dto.getFormId());
        response.setUserId(dto.getUserId());
        response.setResponseData(dto.getResponseData());
        response.setStatus(dto.getStatus());
        response.setSubmittedAt(dto.getSubmittedAt());
        response.setCreatedAt(dto.getCreatedAt());
        response.setUpdatedAt(dto.getUpdatedAt());
        response.setIpAddress(dto.getIpAddress());
        response.setUserAgent(dto.getUserAgent());
        response.setMetadata(dto.getMetadata());
        return response;
    }
}