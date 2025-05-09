package org.microservices.formservice.mappers;

import org.mapstruct.Mapper;
import org.microservices.formservice.DTO.FormDto;
import org.microservices.formservice.entity.Form;

@Mapper(componentModel = "spring")
public interface FormMapper {
    FormDto toDto(Form form);
    Form toEntity(FormDto formDto);
}