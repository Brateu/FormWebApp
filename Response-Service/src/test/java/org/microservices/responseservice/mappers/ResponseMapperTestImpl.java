package org.microservices.responseservice.mappers;

import org.microservices.responseservice.dto.AnsweredQuestionDto;
import org.microservices.responseservice.dto.ResponseDto;
import org.microservices.responseservice.model.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple manual implementation of ResponseMapper for unit tests to avoid
 * relying on annotation processing/generation.
 */
public class ResponseMapperTestImpl implements ResponseMapper {
    @Override
    public ResponseDto toDto(Response response) {
        if (response == null) return null;
        ResponseDto dto = new ResponseDto();
        dto.setId(response.getId());
        dto.setFormId(response.getFormId());
        dto.setUserId(response.getUserId());
        dto.setAnsweredQuestions(null); // As per service behavior
        dto.setStatus(response.getStatus());
        dto.setSubmittedAt(response.getSubmittedAt());
        dto.setCreatedAt(response.getCreatedAt());
        dto.setUpdatedAt(response.getUpdatedAt());
        dto.setIpAddress(response.getIpAddress());
        dto.setUserAgent(response.getUserAgent());
        dto.setMetadata(response.getMetadata());
        return dto;
    }

    @Override
    public Response toEntity(ResponseDto dto) {
        if (dto == null) return null;
        Map<String, Object> responseData = null;
        if (dto.getAnsweredQuestions() != null) {
            responseData = new HashMap<>();
            for (AnsweredQuestionDto a : dto.getAnsweredQuestions()) {
                if (a != null && a.getQuestionId() != null) {
                    responseData.put(a.getQuestionId(), a.getValue());
                }
            }
        }
        return Response.builder()
                .id(dto.getId())
                .formId(dto.getFormId())
                .userId(dto.getUserId())
                .responseData(responseData)
                .status(dto.getStatus())
                .submittedAt(dto.getSubmittedAt())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .ipAddress(dto.getIpAddress())
                .userAgent(dto.getUserAgent())
                .metadata(dto.getMetadata())
                .build();
    }
}
