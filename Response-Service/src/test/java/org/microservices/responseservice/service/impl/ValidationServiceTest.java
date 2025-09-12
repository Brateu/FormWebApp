package org.microservices.responseservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.microservices.responseservice.dto.AnsweredQuestionDto;
import org.microservices.responseservice.dto.OptionDto;
import org.microservices.responseservice.dto.QuestionDefinitionDto;
import org.microservices.responseservice.dto.ResponseDto;
import org.microservices.responseservice.service.validation.AnswerValidator;
import org.microservices.responseservice.service.validation.ChoiceAnswerValidator;
import org.microservices.responseservice.service.validation.DateAnswerValidator;
import org.microservices.responseservice.service.validation.MultiChoiceAnswerValidator;
import org.microservices.responseservice.service.validation.NumberAnswerValidator;
import org.microservices.responseservice.service.validation.TextAnswerValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        List<AnswerValidator> validators = List.of(
                new TextAnswerValidator(),
                new ChoiceAnswerValidator(),
                new MultiChoiceAnswerValidator(),
                new NumberAnswerValidator(),
                new DateAnswerValidator()
        );
        validationService = new ValidationService(validators);
    }

    private ResponseDto baseDto(String status) {
        ResponseDto dto = new ResponseDto();
        dto.setFormId(1L);
        dto.setStatus(status);
        dto.setAnsweredQuestions(new ArrayList<>());
        dto.setQuestionDefinitions(new ArrayList<>());
        return dto;
    }

    @Test
    void draft_allows_missing_required_answers() {
        ResponseDto dto = baseDto("DRAFT");
        // Define one required TEXT question but do not provide an answer
        QuestionDefinitionDto q = new QuestionDefinitionDto();
        q.setId("q1");
        q.setText("Nick");
        q.setType("TEXT");
        q.setRequired(true);
        dto.getQuestionDefinitions().add(q);

        assertDoesNotThrow(() -> validationService.validateResponse(dto));
    }

    @Test
    void submitted_requires_required_answers() {
        ResponseDto dto = baseDto("SUBMITTED");
        QuestionDefinitionDto q = new QuestionDefinitionDto();
        q.setId("q1");
        q.setText("Nick");
        q.setType("TEXT");
        q.setRequired(true);
        dto.getQuestionDefinitions().add(q);

        assertThrows(IllegalArgumentException.class, () -> validationService.validateResponse(dto));
    }

    @Test
    void type_mismatch_throws() {
        ResponseDto dto = baseDto("SUBMITTED");
        // Definition says TEXT
        QuestionDefinitionDto q = new QuestionDefinitionDto();
        q.setId("q1");
        q.setType("TEXT");
        q.setRequired(false);
        dto.getQuestionDefinitions().add(q);
        // Answer says NUMBER
        AnsweredQuestionDto a = new AnsweredQuestionDto();
        a.setQuestionId("q1");
        a.setType("NUMBER");
        a.setValue("hello");
        dto.getAnsweredQuestions().add(a);

        assertThrows(IllegalArgumentException.class, () -> validationService.validateResponse(dto));
    }

    @Test
    void choice_invalid_option_throws() {
        ResponseDto dto = baseDto("SUBMITTED");
        QuestionDefinitionDto q = new QuestionDefinitionDto();
        q.setId("q1");
        q.setType("SINGLE_CHOICE");
        q.setRequired(true);
        q.setOptions(List.of(new OptionDto("1", "Blue")));
        dto.getQuestionDefinitions().add(q);

        AnsweredQuestionDto a = new AnsweredQuestionDto();
        a.setQuestionId("q1");
        a.setType("SINGLE_CHOICE");
        a.setValue("999");
        dto.getAnsweredQuestions().add(a);

        assertThrows(IllegalArgumentException.class, () -> validationService.validateResponse(dto));
    }

    @Test
    void multichoice_min_max_enforced() {
        ResponseDto dto = baseDto("SUBMITTED");
        QuestionDefinitionDto q = new QuestionDefinitionDto();
        q.setId("q1");
        q.setType("MULTI_CHOICE");
        q.setRequired(true);
        q.setOptions(List.of(new OptionDto("a", "A"), new OptionDto("b", "B"), new OptionDto("c", "C")));
        Map<String, Object> rules = new HashMap<>();
        rules.put("minSelections", 2);
        rules.put("maxSelections", 2);
        q.setValidationRules(rules);
        dto.getQuestionDefinitions().add(q);

        // Too few selections
        AnsweredQuestionDto a1 = new AnsweredQuestionDto();
        a1.setQuestionId("q1");
        a1.setType("MULTI_CHOICE");
        a1.setValue(List.of("a"));
        dto.getAnsweredQuestions().add(a1);
        assertThrows(IllegalArgumentException.class, () -> validationService.validateResponse(dto));

        // Adjust to too many
        dto.getAnsweredQuestions().clear();
        AnsweredQuestionDto a2 = new AnsweredQuestionDto();
        a2.setQuestionId("q1");
        a2.setType("MULTI_CHOICE");
        a2.setValue(List.of("a","b","c"));
        dto.getAnsweredQuestions().add(a2);
        assertThrows(IllegalArgumentException.class, () -> validationService.validateResponse(dto));

        // Exactly two (valid)
        dto.getAnsweredQuestions().clear();
        AnsweredQuestionDto a3 = new AnsweredQuestionDto();
        a3.setQuestionId("q1");
        a3.setType("MULTI_CHOICE");
        a3.setValue(List.of("a","b"));
        dto.getAnsweredQuestions().add(a3);
        assertDoesNotThrow(() -> validationService.validateResponse(dto));
    }

    @Test
    void number_min_max_step_allowedList() {
        ResponseDto dto = baseDto("SUBMITTED");
        QuestionDefinitionDto q = new QuestionDefinitionDto();
        q.setId("q1");
        q.setType("NUMBER");
        q.setRequired(true);
        Map<String, Object> rules = new HashMap<>();
        rules.put("min", -4);
        rules.put("max", 20);
        rules.put("step", 3);
        q.setValidationRules(rules);
        dto.getQuestionDefinitions().add(q);

        // invalid step (4 is not in -4..20 step 3 progression)
        AnsweredQuestionDto aBad = new AnsweredQuestionDto();
        aBad.setQuestionId("q1");
        aBad.setType("NUMBER");
        aBad.setValue(4);
        dto.getAnsweredQuestions().add(aBad);
        assertThrows(IllegalArgumentException.class, () -> validationService.validateResponse(dto));

        // valid step 5
        dto.getAnsweredQuestions().clear();
        AnsweredQuestionDto aOk = new AnsweredQuestionDto();
        aOk.setQuestionId("q1");
        aOk.setType("NUMBER");
        aOk.setValue(5);
        dto.getAnsweredQuestions().add(aOk);
        assertDoesNotThrow(() -> validationService.validateResponse(dto));

        // allowed list overrides range
        Map<String, Object> rules2 = new HashMap<>();
        rules2.put("allowedNumbers", List.of(7, 9, 11));
        q.setValidationRules(rules2);
        // 5 should now fail
        assertThrows(IllegalArgumentException.class, () -> validationService.validateResponse(dto));
        dto.getAnsweredQuestions().clear();
        AnsweredQuestionDto aListOk = new AnsweredQuestionDto();
        aListOk.setQuestionId("q1");
        aListOk.setType("NUMBER");
        aListOk.setValue(11);
        dto.getAnsweredQuestions().add(aListOk);
        assertDoesNotThrow(() -> validationService.validateResponse(dto));
    }

    @Test
    void date_minAge_and_bounds() {
        ResponseDto dto = baseDto("SUBMITTED");
        QuestionDefinitionDto q = new QuestionDefinitionDto();
        q.setId("q1");
        q.setType("DATE");
        q.setRequired(true);
        Map<String, Object> rules = new HashMap<>();
        rules.put("minAgeYears", 18);
        rules.put("pastOnly", true);
        q.setValidationRules(rules);
        dto.getQuestionDefinitions().add(q);

        // too young
        AnsweredQuestionDto aYoung = new AnsweredQuestionDto();
        aYoung.setQuestionId("q1");
        aYoung.setType("DATE");
        aYoung.setValue(LocalDate.now().minusYears(10).toString());
        dto.getAnsweredQuestions().add(aYoung);
        assertThrows(IllegalArgumentException.class, () -> validationService.validateResponse(dto));

        // valid age
        dto.getAnsweredQuestions().clear();
        AnsweredQuestionDto aOk = new AnsweredQuestionDto();
        aOk.setQuestionId("q1");
        aOk.setType("DATE");
        aOk.setValue(LocalDate.now().minusYears(20).toString());
        dto.getAnsweredQuestions().add(aOk);
        assertDoesNotThrow(() -> validationService.validateResponse(dto));
    }
}
