package org.microservices.formservice.service;

import org.microservices.formservice.DTO.QuestionDto;

import java.util.List;

public interface QuestionService {


    List<QuestionDto> getQuestionsByForm(Long formId, Long userId);


    QuestionDto getQuestionById(Long id, Long userId);


    QuestionDto createQuestion(Long formId, QuestionDto questionDto, Long userId);


    QuestionDto updateQuestion(Long id, QuestionDto questionDto, Long userId);


    void deleteQuestion(Long id, Long userId);


    QuestionDto cloneQuestion(Long id, Long userId);


    List<QuestionDto> reorderQuestions(Long formId, List<Long> questionIds, Long userId);
}