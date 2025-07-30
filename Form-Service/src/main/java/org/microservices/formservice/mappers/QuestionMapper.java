package org.microservices.formservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.microservices.formservice.DTO.QuestionDto;
import org.microservices.formservice.entity.Question;

import java.util.List;

/**
 * QuestionMapper is a MapStruct mapper interface for converting between the Question
 * entity and QuestionDto. It provides methods to facilitate the transformation of question
 * data between different layers of the application.
 *
 * This mapper uses the `spring` component model to enable Spring's dependency injection
 * for its generated implementation. It defines specific mappings and customizations
 * where property names differ between the source and target objects or where fields
 * need to be ignored during mapping.
 *
 * Methods:
 * - toDto(Question question): Converts a Question entity into its corresponding QuestionDto.
 *   Maps the `form.id` field from the source entity to the `formId` field in the target DTO.
 *
 * - toDtoList(List<Question> questions): Converts a list of Question entities into a list of QuestionDto objects.
 *
 * - toEntity(QuestionDto questionDto): Converts a QuestionDto into its corresponding Question entity.
 *
 * - updateEntityFromDto(QuestionDto dto, @MappingTarget Question question): Updates an existing Question
 *   entity with values from a QuestionDto. Ignores the `form` field on the target entity during the update.
 */
@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "formId", source = "form.id")
    QuestionDto toDto(Question question);

    List<QuestionDto> toDtoList(List<Question> questions);

    Question toEntity(QuestionDto questionDto);

    @Mapping(target = "form", ignore = true)
    void updateEntityFromDto(QuestionDto dto, @MappingTarget Question question);
}