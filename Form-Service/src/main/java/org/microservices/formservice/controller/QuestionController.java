package org.microservices.formservice.controller;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.QuestionDto;
import org.microservices.formservice.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/form/{formId}")
    public ResponseEntity<List<QuestionDto>> getQuestionsByForm(
            @PathVariable Long formId,
            @RequestHeader("X-User-ID") Long userId) {
        List<QuestionDto> questions = questionService.getQuestionsByForm(formId, userId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestionById(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long userId) {
        QuestionDto question = questionService.getQuestionById(id, userId);
        return ResponseEntity.ok(question);
    }

    @PostMapping("/form/{formId}")
    public ResponseEntity<QuestionDto> createQuestion(
            @PathVariable Long formId,
            @RequestBody QuestionDto questionDto,
            @RequestHeader("X-User-ID") Long userId) {
        QuestionDto createdQuestion = questionService.createQuestion(formId, questionDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionDto> updateQuestion(
            @PathVariable Long id,
            @RequestBody QuestionDto questionDto,
            @RequestHeader("X-User-ID") Long userId) {
        QuestionDto updatedQuestion = questionService.updateQuestion(id, questionDto, userId);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long userId) {
        questionService.deleteQuestion(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/clone")
    public ResponseEntity<QuestionDto> cloneQuestion(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long userId) {
        QuestionDto clonedQuestion = questionService.cloneQuestion(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(clonedQuestion);
    }

    @PutMapping("/form/{formId}/reorder")
    public ResponseEntity<List<QuestionDto>> reorderQuestions(
            @PathVariable Long formId,
            @RequestBody List<Long> questionIds,
            @RequestHeader("X-User-ID") Long userId) {
        List<QuestionDto> reorderedQuestions = questionService.reorderQuestions(formId, questionIds, userId);
        return ResponseEntity.ok(reorderedQuestions);
    }
}