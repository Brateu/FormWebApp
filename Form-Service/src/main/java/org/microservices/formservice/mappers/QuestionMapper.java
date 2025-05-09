package org.microservices.formservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.microservices.formservice.DTO.QuestionDto;
import org.microservices.formservice.entity.Question;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {


    QuestionDto toDto(Question question);
    List<QuestionDto> toDtoList(List<Question> questions);
    Question toEntity(QuestionDto questionDto);
    void updateEntityFromDto(QuestionDto dto, @MappingTarget Question question);
}