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
 * Mapper for converting between Response entity and ResponseDto using MapStruct.
 * Located in the 'mappers' package as requested.
 */
@Mapper(componentModel = "spring")
public interface ResponseMapper {

    @Mapping(target = "answeredQuestions", ignore = true)
    ResponseDto toDto(Response response);

    @Mapping(target = "responseData", ignore = true)
    Response toEntity(ResponseDto dto);

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
