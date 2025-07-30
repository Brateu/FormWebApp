package org.microservices.formservice.controller;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.QuestionDto;
import org.microservices.formservice.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing questions in forms.
 * Provides endpoints for retrieving, creating, updating, deleting, cloning, and reordering questions.
 * Questions are always associated with a specific form.
 */
@RestController
@RequestMapping("/api/forms/{formId}/questions")
@RequiredArgsConstructor
public class QuestionController {

    /**
     * Service for question-related operations.
     */
    private final QuestionService questionService;

    /**
     * Retrieves all questions for a specific form.
     * 
     * @param formId The ID of the form whose questions to retrieve
     * @param userId The ID of the user making the request
     * @return A list of question DTOs
     */
    @GetMapping
    public ResponseEntity<List<QuestionDto>> getQuestionsByForm(
            @PathVariable Long formId,
            @RequestHeader("X-User-ID") Long userId) {
        List<QuestionDto> questions = questionService.getQuestionsByForm(formId, userId);
        return ResponseEntity.ok(questions);
    }

    /**
     * Retrieves a specific question by its ID.
     * 
     * @param formId The ID of the form containing the question
     * @param questionId The ID of the question to retrieve
     * @param userId The ID of the user making the request
     * @return The question DTO if found
     */
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDto> getQuestionById(
            @PathVariable Long formId,
            @PathVariable Long questionId,
            @RequestHeader("X-User-ID") Long userId) {
        QuestionDto question = questionService.getQuestionById(questionId, userId);
        return ResponseEntity.ok(question);
    }

    /**
     * Creates a new question in a form.
     * 
     * @param formId The ID of the form to add the question to
     * @param questionDto The question data to create
     * @param userId The ID of the user making the request
     * @return The created question DTO
     */
    @PostMapping
    public ResponseEntity<QuestionDto> createQuestion(
            @PathVariable Long formId,
            @RequestBody QuestionDto questionDto,
            @RequestHeader("X-User-ID") Long userId) {
        QuestionDto createdQuestion = questionService.createQuestion(formId, questionDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
    }

    /**
     * Updates an existing question.
     * 
     * @param formId The ID of the form containing the question
     * @param questionId The ID of the question to update
     * @param questionDto The updated question data
     * @param userId The ID of the user making the request
     * @return The updated question DTO
     */
    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionDto> updateQuestion(
            @PathVariable Long formId,
            @PathVariable Long questionId,
            @RequestBody QuestionDto questionDto,
            @RequestHeader("X-User-ID") Long userId) {
        questionDto.setFormId(formId);
        questionDto.setId(questionId);
        QuestionDto updatedQuestion = questionService.updateQuestion(questionId, questionDto, userId);
        return ResponseEntity.ok(updatedQuestion);
    }

    /**
     * Deletes a question.
     * 
     * @param formId The ID of the form containing the question
     * @param questionId The ID of the question to delete
     * @param userId The ID of the user making the request
     * @return No content response
     */
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long formId,
            @PathVariable Long questionId,
            @RequestHeader("X-User-ID") Long userId) {
        questionService.deleteQuestion(questionId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates a clone of an existing question.
     * 
     * @param formId The ID of the form containing the question
     * @param questionId The ID of the question to clone
     * @param userId The ID of the user making the request
     * @return The cloned question DTO
     */
    @PostMapping("/{questionId}/clone")
    public ResponseEntity<QuestionDto> cloneQuestion(
            @PathVariable Long formId,
            @PathVariable Long questionId,
            @RequestHeader("X-User-ID") Long userId) {
        QuestionDto clonedQuestion = questionService.cloneQuestion(questionId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(clonedQuestion);
    }

    /**
     * Reorders questions within a form.
     * 
     * @param formId The ID of the form containing the questions
     * @param questionIds The ordered list of question IDs
     * @param userId The ID of the user making the request
     * @return The list of reordered question DTOs
     */
    @PutMapping("/reorder")
    public ResponseEntity<List<QuestionDto>> reorderQuestions(
            @PathVariable Long formId,
            @RequestBody List<Long> questionIds,
            @RequestHeader("X-User-ID") Long userId) {
        List<QuestionDto> reorderedQuestions = questionService.reorderQuestions(formId, questionIds, userId);
        return ResponseEntity.ok(reorderedQuestions);
    }
}