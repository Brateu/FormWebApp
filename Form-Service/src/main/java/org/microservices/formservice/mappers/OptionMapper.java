package org.microservices.formservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.microservices.formservice.DTO.OptionDto;
import org.microservices.formservice.entity.Option;

/**
 * OptionMapper is an interface for mapping between the Option entity and the OptionDto.
 * This mapper facilitates the transformation of data between the persistence layer (Option entity)
 * and the application/business layer (OptionDto).
 *
 * It uses MapStruct to generate the implementation automatically at compile-time
 * and adopts the `spring` component model for seamless integration with Spring's dependency injection.
 *
 * Methods:
 * - toDto(Option option): Converts an Option entity into its corresponding OptionDto.
 *   Performs a mapping from the `question.id` field in the Option entity to the `questionId` field in the DTO.
 *
 * - toEntity(OptionDto optionDto): Converts an OptionDto into its corresponding Option entity.
 *   Ignores the `question` field during the conversion process.
 */
@Mapper(componentModel = "spring")
public interface OptionMapper {
    @Mapping(target = "questionId", source = "question.id")
    OptionDto toDto(Option option);

    @Mapping(target = "question", ignore = true)
    Option toEntity(OptionDto optionDto);
}