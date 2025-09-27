package org.microservices.responseservice.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.microservices.responseservice.dto.AnsweredQuestionDto;
import org.microservices.responseservice.dto.ResponseDto;
import org.microservices.responseservice.model.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * MapStruct-based mapper between Response entity and ResponseDto.
 * Populates responseData from answeredQuestions after mapping and vice versa when needed.
 */
@Mapper(componentModel = "spring")
public interface ResponseMapper {

    /**
     * Convert entity to DTO. answeredQuestions will be supplied by a custom step if needed.
     */
    @Mapping(target = "answeredQuestions", ignore = true)
    ResponseDto toDto(Response response);

    /**
     * Convert DTO to entity. responseData is derived in @AfterMapping.
     */
    @Mapping(target = "responseData", ignore = true)
    Response toEntity(ResponseDto dto);

    /**
     * After-mapping hook to populate entity.responseData from dto.answeredQuestions.
     */
    @AfterMapping
    default void mapAnsweredQuestionsToResponseData(ResponseDto dto, @MappingTarget Response response) {
        if (dto == null) return;
        if (dto.getAnsweredQuestions() == null) return;
        Map<String, Object> responseData = new HashMap<>();
        for (AnsweredQuestionDto a : dto.getAnsweredQuestions()) {
            if (a != null && a.getQuestionId() != null) {
                responseData.put(a.getQuestionId(), a.getValue());
            }
        }
        if (!responseData.isEmpty()) {
            response.setResponseData(responseData);
        } else {
            response.setResponseData(null);
        }
    }
}
