package org.microservices.formservice.mappers;

import javax.annotation.processing.Generated;
import org.microservices.formservice.DTO.CollaboratorDto;
import org.microservices.formservice.entity.Collaborator;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-09T12:38:50+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class CollaboratorMapperImpl implements CollaboratorMapper {

    @Override
    public CollaboratorDto toDto(Collaborator collaborator) {
        if ( collaborator == null ) {
            return null;
        }

        CollaboratorDto.CollaboratorDtoBuilder collaboratorDto = CollaboratorDto.builder();

        collaboratorDto.id( collaborator.getId() );
        collaboratorDto.userId( collaborator.getUserId() );
        collaboratorDto.role( collaborator.getRole() );

        return collaboratorDto.build();
    }

    @Override
    public Collaborator toEntity(CollaboratorDto collaboratorDto) {
        if ( collaboratorDto == null ) {
            return null;
        }

        Collaborator.CollaboratorBuilder collaborator = Collaborator.builder();

        collaborator.id( collaboratorDto.getId() );
        collaborator.userId( collaboratorDto.getUserId() );
        collaborator.role( collaboratorDto.getRole() );

        return collaborator.build();
    }
}
