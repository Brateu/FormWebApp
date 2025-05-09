package org.microservices.formservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.microservices.formservice.DTO.OptionDto;
import org.microservices.formservice.entity.Option;

@Mapper(componentModel = "spring")
public interface OptionMapper {
    OptionDto toDto(Option option);

    Option toEntity(OptionDto optionDto);
}