package org.microservices.formservice.mappers;

import javax.annotation.processing.Generated;
import org.microservices.formservice.DTO.FormDto;
import org.microservices.formservice.entity.Form;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-30T15:48:37+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class FormMapperImpl implements FormMapper {

    @Override
    public FormDto toDto(Form form) {
        if ( form == null ) {
            return null;
        }

        FormDto.FormDtoBuilder formDto = FormDto.builder();

        formDto.id( form.getId() );
        formDto.name( form.getName() );
        formDto.description( form.getDescription() );
        formDto.allowAnonymous( form.isAllowAnonymous() );
        formDto.responseLimit( form.getResponseLimit() );
        formDto.locked( form.isLocked() );
        formDto.createdAt( form.getCreatedAt() );
        formDto.updatedAt( form.getUpdatedAt() );
        formDto.createdBy( form.getCreatedBy() );
        formDto.status( form.getStatus() );
        formDto.visibility( form.getVisibility() );

        return formDto.build();
    }

    @Override
    public Form toEntity(FormDto formDto) {
        if ( formDto == null ) {
            return null;
        }

        Form.FormBuilder form = Form.builder();

        form.id( formDto.getId() );
        form.name( formDto.getName() );
        form.description( formDto.getDescription() );
        form.allowAnonymous( formDto.isAllowAnonymous() );
        form.responseLimit( formDto.getResponseLimit() );
        form.locked( formDto.isLocked() );
        form.createdAt( formDto.getCreatedAt() );
        form.updatedAt( formDto.getUpdatedAt() );
        form.createdBy( formDto.getCreatedBy() );
        form.status( formDto.getStatus() );
        form.visibility( formDto.getVisibility() );

        return form.build();
    }
}
