package org.microservices.formservice.mappers;

import org.microservices.formservice.DTO.FormDto;
import org.microservices.formservice.DTO.OptionDto;
import org.microservices.formservice.DTO.QuestionDto;
import org.microservices.formservice.entity.Form;
import org.microservices.formservice.entity.Option;
import org.microservices.formservice.entity.Question;
import org.springframework.stereotype.Component;

@Component
public class FormToDto {
    public OptionDto optionDto(Option option){
        if (option == null){
            return null;
        }

        return new OptionDto(
                option.getId(), option.getText(), option.getImageUrl(), option.getQuestion().getId()
        );
    }

    public QuestionDto toDto(Question question) {
        if ( question == null ) {
            return null;
        }

        return new QuestionDto(
                question.getId(), question.getText(), question.getType(),question.isRequired(),question.getOrderIndex(),question.getForm().getId(), question.getUserId(), question.getImageUrl(), question.getOptions().stream().map((this::optionDto)).toList()
        );
    }

    public FormDto formDto (Form form){
        return new FormDto(
                form.getId(), form.getName(), form.getDescription(), form.isAllowAnonymous(), form.getResponseLimit(), form.isLocked(), form.getCreatedAt(),form.getUpdatedAt(), form.getCreatedBy(), form.getStatus(),form.getVisibility(),form.getQuestions().stream().map((this::toDto)).toList()
        );
    }
}
