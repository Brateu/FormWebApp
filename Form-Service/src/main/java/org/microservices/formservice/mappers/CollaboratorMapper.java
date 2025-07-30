package org.microservices.formservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.microservices.formservice.DTO.CollaboratorDto;
import org.microservices.formservice.entity.Collaborator;

/**
 * CollaboratorMapper is a MapStruct mapper interface for converting between
 * Collaborator entity and CollaboratorDto. It defines methods for mapping
 * entity to DTO and vice versa, facilitating the transformation of data between
 * layers of the application.
 *
 * This mapper uses the `spring` component model to enable Spring's dependency
 * injection for its implementation. It also defines specific mappings where
 * differences in property names exist between the source and target objects.
 */
@Mapper(componentModel = "spring")
public interface CollaboratorMapper {
    @Mapping(target = "formId", source = "form.id")
    CollaboratorDto toDto(Collaborator collaborator);
    Collaborator toEntity(CollaboratorDto collaboratorDto);
}