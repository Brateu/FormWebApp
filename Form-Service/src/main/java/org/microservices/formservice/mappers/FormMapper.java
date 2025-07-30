package org.microservices.formservice.mappers;

import org.mapstruct.Mapper;
import org.microservices.formservice.DTO.FormDto;
import org.microservices.formservice.entity.Form;

/**
 * FormMapper is an interface for mapping between the Form entity and the FormDto.
 * This mapper simplifies the transformation of data between the persistence layer (Form entity)
 * and the application/business layer (FormDto).
 *
 * It leverages MapStruct to automatically generate the implementation at compile-time,
 * and uses the `spring` component model for integration with Spring's dependency injection.
 *
 * Methods:
 * - toDto(Form form): Converts a Form entity to its corresponding FormDto.
 * - toEntity(FormDto formDto): Converts a FormDto to its corresponding Form entity.
 */
@Mapper(componentModel = "spring")
public interface FormMapper {
    FormDto toDto(Form form);
    Form toEntity(FormDto formDto);
}