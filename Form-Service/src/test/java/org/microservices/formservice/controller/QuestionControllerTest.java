package org.microservices.formservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.microservices.formservice.DTO.QuestionDto;
import org.microservices.formservice.enums.QuestionType;
import org.microservices.formservice.service.QuestionService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuestionControllerTest {

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuestionController editController;

    private QuestionDto questionDto;
    private List<QuestionDto> questionDtoList;
    private Long userId;
    private Long formId;

    @BeforeEach
    void setUp() {
        userId = 123L;
        formId = 456L;

        questionDto = QuestionDto.builder()
                .id(1L)
                .text("Test Question")
                .type(QuestionType.SHORT_TEXT)
                .required(true)
                .orderIndex(1)
                .formId(formId)
                .build();

        questionDtoList = new ArrayList<>();
        questionDtoList.add(questionDto);
    }

    @Test
    void testGetQuestionsByForm() {
        // Setup
        when(questionService.getQuestionsByForm(anyLong(), anyLong())).thenReturn(questionDtoList);

        // Execute
        ResponseEntity<List<QuestionDto>> response = editController.getQuestionsByForm(formId, userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(questionDtoList, response.getBody());
        verify(questionService).getQuestionsByForm(formId, userId);
    }

    // Commented out test - needs to be fixed
    /*
    @Test
    void testGetQuestionById() {
        // Setup
        when(questionService.getQuestionById(anyLong(), anyLong())).thenReturn(questionDto);

        // Execute
        ResponseEntity<QuestionDto> response = editController.getQuestionById(formId, 1L, userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(questionDto, response.getBody());
        verify(questionService).getQuestionById(1L, userId);
    }
    */

    @Test
    void testCreateQuestion() {
        // Setup
        when(questionService.createQuestion(anyLong(), any(QuestionDto.class), anyLong())).thenReturn(questionDto);

        // Execute
        ResponseEntity<QuestionDto> response = editController.createQuestion(formId, questionDto, userId);

        // Verify
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(questionDto, response.getBody());
        verify(questionService).createQuestion(formId, questionDto, userId);
    }

    @Test
    void testUpdateQuestion() {
        // Setup
        when(questionService.updateQuestion(anyLong(), any(QuestionDto.class), anyLong())).thenReturn(questionDto);

        // Execute
        ResponseEntity<QuestionDto> response = editController.updateQuestion(formId, 1L, questionDto, userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(questionDto, response.getBody());
        verify(questionService).updateQuestion(1L, questionDto, userId);
    }

    @Test
    void testDeleteQuestion() {
        // Execute
        ResponseEntity<Void> response = editController.deleteQuestion(formId, 1L, userId);

        // Verify
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(questionService).deleteQuestion(1L, userId);
    }

    @Test
    void testCloneQuestion() {
        // Setup
        when(questionService.cloneQuestion(anyLong(), anyLong())).thenReturn(questionDto);

        // Execute
        ResponseEntity<QuestionDto> response = editController.cloneQuestion(formId, 1L, userId);

        // Verify
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(questionDto, response.getBody());
        verify(questionService).cloneQuestion(1L, userId);
    }

    @Test
    void testReorderQuestions() {
        // Setup
        List<Long> questionIds = List.of(1L, 2L, 3L);
        when(questionService.reorderQuestions(anyLong(), any(), anyLong())).thenReturn(questionDtoList);

        // Execute
        ResponseEntity<List<QuestionDto>> response = editController.reorderQuestions(formId, questionIds, userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(questionDtoList, response.getBody());
        verify(questionService).reorderQuestions(formId, questionIds, userId);
    }
}