package org.microservices.responseservice.mappers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.microservices.responseservice.dto.AnsweredQuestionDto;
import org.microservices.responseservice.dto.QuestionDefinitionDto;
import org.microservices.responseservice.dto.ResponseDto;
import org.microservices.responseservice.model.Response;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Manual mapper for converting between Response entity and ResponseDto.
 * Handles polymorphic responseData map and optional question definitions embedded in metadata.
 */
@Component
@RequiredArgsConstructor
public class Mapper {
    private final ObjectMapper objectMapper;
    

    /**
     * Map ResponseDto to Response entity.
     * Copies scalar fields, flattens answeredQuestions into responseData map,
     * and carries questionDefinitions via metadata under key 'questionDefinitions'.
     * @param dto source DTO
     * @return mapped entity or null
     */
    public Response toEntity(ResponseDto dto) {
        if (dto == null) return null;

        Response e = new Response();
        e.setId(dto.getId());
        e.setFormId(dto.getFormId());
        e.setUserId(dto.getUserId());
        e.setStatus(dto.getStatus());
        e.setSubmittedAt(dto.getSubmittedAt());
        e.setCreatedAt(dto.getCreatedAt());
        e.setUpdatedAt(dto.getUpdatedAt());
        e.setIpAddress(dto.getIpAddress());
        e.setUserAgent(dto.getUserAgent());
        
        e.setResponseData(toResponseData(dto.getAnsweredQuestions()));
        
        Map<String, Object> meta = dto.getMetadata() != null
                ? new HashMap<>(dto.getMetadata())
                : new HashMap<>();
        if (dto.getQuestionDefinitions() != null) {
            meta.put("questionDefinitions", dto.getQuestionDefinitions());
        }
        e.setMetadata(meta);

        return e;
    }

    /**
     * Convert a list of AnsweredQuestionDto into a responseData map keyed by questionId.
     */
    private Map<String, Object> toResponseData(List<AnsweredQuestionDto> list) {
        if (list == null) return Collections.emptyMap();
        Map<String, Object> m = new LinkedHashMap<>();
        for (AnsweredQuestionDto aq : list) {
            if (aq == null || aq.getQuestionId() == null) continue;
            m.put(aq.getQuestionId(), aq.getValue());
        }
        return m;
    }

    /**
     * Map Response entity to ResponseDto.
     * Copies scalar fields and expands responseData into answeredQuestions.
     * If metadata contains 'questionDefinitions', uses it to enrich answered questions and preserve order.
     * @param e source entity
     * @return mapped DTO or null
     */
    public ResponseDto toDto(Response e) {
        if (e == null) return null;

        ResponseDto dto = new ResponseDto();
        dto.setId(e.getId());
        dto.setFormId(e.getFormId());
        dto.setUserId(e.getUserId());
        dto.setStatus(e.getStatus());
        dto.setSubmittedAt(e.getSubmittedAt());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        dto.setIpAddress(e.getIpAddress());
        dto.setUserAgent(e.getUserAgent());
        dto.setMetadata(e.getMetadata());

        List<QuestionDefinitionDto> defs = readQuestionDefinitions(e.getMetadata());
        dto.setQuestionDefinitions(defs);

        // map responseData entries to answeredQuestions preserving definition order when available
        Map<String, Object> data = e.getResponseData() != null ? e.getResponseData() : Collections.emptyMap();
        if (!data.isEmpty()) {
            List<AnsweredQuestionDto> answered;

            if (defs != null && !defs.isEmpty()) {
                Map<String, QuestionDefinitionDto> byId = defs.stream()
                        .collect(Collectors.toMap(QuestionDefinitionDto::getId, Function.identity()));

                answered = new ArrayList<>(defs.size());
                for (QuestionDefinitionDto d : defs) {
                    AnsweredQuestionDto aq = new AnsweredQuestionDto();
                    aq.setQuestionId(d.getId());
                    aq.setType(d.getType());
                    aq.setValue(data.get(d.getId()));
                    answered.add(aq);
                }

                for (Map.Entry<String, Object> en : data.entrySet()) {
                    if (!byId.containsKey(en.getKey())) {
                        AnsweredQuestionDto extra = new AnsweredQuestionDto();
                        extra.setQuestionId(en.getKey());
                        extra.setType(null);
                        extra.setValue(en.getValue());
                        answered.add(extra);
                    }
                }
            } else {
                answered = data.entrySet().stream().map(en -> {
                    AnsweredQuestionDto aq = new AnsweredQuestionDto();
                    aq.setQuestionId(en.getKey());
                    aq.setType(null);
                    aq.setValue(en.getValue());
                    return aq;
                }).collect(Collectors.toList());
            }

            dto.setAnsweredQuestions(answered);
        } else {
            dto.setAnsweredQuestions(Collections.emptyList());
        }

        return dto;
    }

    /**
     * Read question definitions from metadata map under key 'questionDefinitions'.
     * Accepts either a real List<QuestionDefinitionDto> or a list of maps convertible via ObjectMapper.
     */
    @SuppressWarnings("unchecked")
    private List<QuestionDefinitionDto> readQuestionDefinitions(Map<String, Object> meta) {
        if (meta == null) return null;
        Object raw = meta.get("questionDefinitions");
        if (raw == null) return null;

        try {
            if (raw instanceof List<?> list && (list.isEmpty() || list.get(0) instanceof QuestionDefinitionDto)) {
                return (List<QuestionDefinitionDto>) raw;
            }
            return objectMapper.convertValue(raw, new TypeReference<List<QuestionDefinitionDto>>() {});
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
