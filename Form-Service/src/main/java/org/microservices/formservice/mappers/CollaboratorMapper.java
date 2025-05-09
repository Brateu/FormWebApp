package org.microservices.formservice.mappers;

import org.mapstruct.Mapper;
import org.microservices.formservice.DTO.CollaboratorDto;
import org.microservices.formservice.entity.Collaborator;

@Mapper(componentModel = "spring")
public interface CollaboratorMapper {
    CollaboratorDto toDto(Collaborator collaborator);
    Collaborator toEntity(CollaboratorDto collaboratorDto);
}