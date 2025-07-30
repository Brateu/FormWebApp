package org.microservices.formservice.mappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.microservices.formservice.DTO.OptionDto;
import org.microservices.formservice.DTO.QuestionDto;
import org.microservices.formservice.entity.Form;
import org.microservices.formservice.entity.Option;
import org.microservices.formservice.entity.Question;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-30T15:48:37+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23 (Oracle Corporation)"
)
@Component
public class QuestionMapperImpl implements QuestionMapper {

    @Override
    public QuestionDto toDto(Question question) {
        if ( question == null ) {
            return null;
        }

        QuestionDto.QuestionDtoBuilder questionDto = QuestionDto.builder();

        questionDto.formId( questionFormId( question ) );
        questionDto.id( question.getId() );
        questionDto.text( question.getText() );
        questionDto.type( question.getType() );
        questionDto.required( question.isRequired() );
        questionDto.orderIndex( question.getOrderIndex() );
        questionDto.userId( question.getUserId() );
        questionDto.options( optionListToOptionDtoList( question.getOptions() ) );

        return questionDto.build();
    }

    @Override
    public List<QuestionDto> toDtoList(List<Question> questions) {
        if ( questions == null ) {
            return null;
        }

        List<QuestionDto> list = new ArrayList<QuestionDto>( questions.size() );
        for ( Question question : questions ) {
            list.add( toDto( question ) );
        }

        return list;
    }

    @Override
    public Question toEntity(QuestionDto questionDto) {
        if ( questionDto == null ) {
            return null;
        }

        Question.QuestionBuilder question = Question.builder();

        question.id( questionDto.getId() );
        question.text( questionDto.getText() );
        question.type( questionDto.getType() );
        question.required( questionDto.isRequired() );
        question.orderIndex( questionDto.getOrderIndex() );
        question.userId( questionDto.getUserId() );
        question.options( optionDtoListToOptionList( questionDto.getOptions() ) );

        return question.build();
    }

    @Override
    public void updateEntityFromDto(QuestionDto dto, Question question) {
        if ( dto == null ) {
            return;
        }

        question.setId( dto.getId() );
        question.setText( dto.getText() );
        question.setType( dto.getType() );
        question.setRequired( dto.isRequired() );
        question.setOrderIndex( dto.getOrderIndex() );
        question.setUserId( dto.getUserId() );
        if ( question.getOptions() != null ) {
            List<Option> list = optionDtoListToOptionList( dto.getOptions() );
            if ( list != null ) {
                question.getOptions().clear();
                question.getOptions().addAll( list );
            }
            else {
                question.setOptions( null );
            }
        }
        else {
            List<Option> list = optionDtoListToOptionList( dto.getOptions() );
            if ( list != null ) {
                question.setOptions( list );
            }
        }
    }

    private Long questionFormId(Question question) {
        if ( question == null ) {
            return null;
        }
        Form form = question.getForm();
        if ( form == null ) {
            return null;
        }
        Long id = form.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected OptionDto optionToOptionDto(Option option) {
        if ( option == null ) {
            return null;
        }

        OptionDto.OptionDtoBuilder optionDto = OptionDto.builder();

        optionDto.id( option.getId() );
        optionDto.text( option.getText() );
        optionDto.imageUrl( option.getImageUrl() );

        return optionDto.build();
    }

    protected List<OptionDto> optionListToOptionDtoList(List<Option> list) {
        if ( list == null ) {
            return null;
        }

        List<OptionDto> list1 = new ArrayList<OptionDto>( list.size() );
        for ( Option option : list ) {
            list1.add( optionToOptionDto( option ) );
        }

        return list1;
    }

    protected Option optionDtoToOption(OptionDto optionDto) {
        if ( optionDto == null ) {
            return null;
        }

        Option.OptionBuilder option = Option.builder();

        option.id( optionDto.getId() );
        option.text( optionDto.getText() );
        option.imageUrl( optionDto.getImageUrl() );

        return option.build();
    }

    protected List<Option> optionDtoListToOptionList(List<OptionDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Option> list1 = new ArrayList<Option>( list.size() );
        for ( OptionDto optionDto : list ) {
            list1.add( optionDtoToOption( optionDto ) );
        }

        return list1;
    }
}
