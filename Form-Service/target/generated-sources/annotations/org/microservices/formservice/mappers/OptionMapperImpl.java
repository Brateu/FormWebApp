package org.microservices.formservice.mappers;

import javax.annotation.processing.Generated;
import org.microservices.formservice.DTO.OptionDto;
import org.microservices.formservice.entity.Option;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-09T12:38:50+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class OptionMapperImpl implements OptionMapper {

    @Override
    public OptionDto toDto(Option option) {
        if ( option == null ) {
            return null;
        }

        OptionDto.OptionDtoBuilder optionDto = OptionDto.builder();

        optionDto.id( option.getId() );
        optionDto.text( option.getText() );
        optionDto.imageUrl( option.getImageUrl() );

        return optionDto.build();
    }

    @Override
    public Option toEntity(OptionDto optionDto) {
        if ( optionDto == null ) {
            return null;
        }

        Option.OptionBuilder option = Option.builder();

        option.id( optionDto.getId() );
        option.text( optionDto.getText() );
        option.imageUrl( optionDto.getImageUrl() );

        return option.build();
    }
}
