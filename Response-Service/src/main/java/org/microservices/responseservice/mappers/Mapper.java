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

@Component
@RequiredArgsConstructor
public class Mapper {
    private final ObjectMapper objectMapper;

    /* =============== DTO -> ENTITY =============== */

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

        // answeredQuestions[] -> responseData map
        e.setResponseData(toResponseData(dto.getAnsweredQuestions()));

        // metadata (+ questionDefinitions u metapolje)
        Map<String, Object> meta = dto.getMetadata() != null
                ? new HashMap<>(dto.getMetadata())
                : new HashMap<>();
        if (dto.getQuestionDefinitions() != null) {
            // Može direktno da se snimi kao lista DTO objekata — Mongo će je upisati kao ugnežđene dokumente
            meta.put("questionDefinitions", dto.getQuestionDefinitions());
        }
        e.setMetadata(meta);

        return e;
    }

    private Map<String, Object> toResponseData(List<AnsweredQuestionDto> list) {
        if (list == null) return Collections.emptyMap();
        Map<String, Object> m = new LinkedHashMap<>();
        for (AnsweredQuestionDto aq : list) {
            if (aq == null || aq.getQuestionId() == null) continue;
            // vrednost ostavi “as is” (String, List, broj…), jer je polymorphic
            m.put(aq.getQuestionId(), aq.getValue());
        }
        return m;
    }

    /* =============== ENTITY -> DTO =============== */

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

        // questionDefinitions iz metadata (ako postoje)
        List<QuestionDefinitionDto> defs = readQuestionDefinitions(e.getMetadata());
        dto.setQuestionDefinitions(defs);

        // responseData -> answeredQuestions[]
        Map<String, Object> data = e.getResponseData() != null ? e.getResponseData() : Collections.emptyMap();
        if (!data.isEmpty()) {
            List<AnsweredQuestionDto> answered;

            if (defs != null && !defs.isEmpty()) {
                Map<String, QuestionDefinitionDto> byId = defs.stream()
                        .collect(Collectors.toMap(QuestionDefinitionDto::getId, Function.identity()));

                // zadrži redosled po defs (lepši prikaz na UI)
                answered = new ArrayList<>(defs.size());
                for (QuestionDefinitionDto d : defs) {
                    AnsweredQuestionDto aq = new AnsweredQuestionDto();
                    aq.setQuestionId(d.getId());
                    aq.setType(d.getType());
                    aq.setValue(data.get(d.getId()));
                    answered.add(aq);
                }

                // ubaci eventualne dodatne ključeve iz responseData kojih nema u defs
                for (Map.Entry<String, Object> en : data.entrySet()) {
                    if (!byId.containsKey(en.getKey())) {
                        AnsweredQuestionDto extra = new AnsweredQuestionDto();
                        extra.setQuestionId(en.getKey());
                        extra.setType(null); // ne znamo tip bez definicije
                        extra.setValue(en.getValue());
                        answered.add(extra);
                    }
                }
            } else {
                // bez definicija — samo prevedi mapu u listu
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

    @SuppressWarnings("unchecked")
    private List<QuestionDefinitionDto> readQuestionDefinitions(Map<String, Object> meta) {
        if (meta == null) return null;
        Object raw = meta.get("questionDefinitions");
        if (raw == null) return null;

        try {
            // ako je već prava lista
            if (raw instanceof List<?> list && (list.isEmpty() || list.get(0) instanceof QuestionDefinitionDto)) {
                return (List<QuestionDefinitionDto>) raw;
            }
            // ako je lista Map-ova (uobičajeno pri čitanju iz Mongo)
            return objectMapper.convertValue(raw, new TypeReference<List<QuestionDefinitionDto>>() {});
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
