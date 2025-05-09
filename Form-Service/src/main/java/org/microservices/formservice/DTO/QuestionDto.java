package org.microservices.formservice.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microservices.formservice.enums.QuestionType;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    private Long id;

    @NotBlank(message = "Question text cannot be empty")
    private String text;

    @NotNull(message = "Question type is required")
    private QuestionType type;

    private boolean required;

    private Integer orderIndex;

    private Long formId;

    private String imageUrl;

    private List<OptionDto> options = new ArrayList<>();
}