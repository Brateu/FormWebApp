
package org.microservices.formservice.controller;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.OptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.microservices.formservice.service.OptionService;

import java.util.List;

/**
 * REST controller for managing options for questions in forms.
 * Provides endpoints for retrieving, creating, updating, and deleting options.
 * Options are always associated with a specific question within a form.
 */
@RestController
@RequestMapping("/api/forms/{formId}/questions/{questionId}/options")
@RequiredArgsConstructor
public class OptionController {

    /**
     * Service for option-related operations.
     */
    private final OptionService optionService;

    /**
     * Retrieves all options for a specific question.
     *
     * @param formId The ID of the form containing the question
     * @param questionId The ID of the question of whose options to retrieve
     * @param userId The ID of the user making the request
     * @return A list of option DTOs
     */
    @GetMapping
    public ResponseEntity<List<OptionDto>> getOptions(
            @PathVariable Long formId,
            @PathVariable Long questionId,
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(optionService.getOptionsByQuestion(questionId, userId));
    }

    /**
     * Creates a new option for a question.
     *
     * @param formId The ID of the form containing the question
     * @param questionId The ID of the question to add the option to
     * @param dto The option data to create
     * @param userId The ID of the user making the request
     * @return The created option DTO
     */
    @PostMapping
    public ResponseEntity<OptionDto> create(
            @PathVariable Long formId,
            @PathVariable Long questionId,
            @RequestBody OptionDto dto,
            @RequestHeader("X-User-ID") Long userId) {
        dto.setQuestionId(questionId);
        return ResponseEntity.ok(optionService.createOption(questionId, dto, userId));
    }

    /**
     * Updates an existing option.
     *
     * @param formId The ID of the form containing the question
     * @param questionId The ID of the question containing the option
     * @param optionId The ID of the option to update
     * @param dto The updated option data
     * @param userId The ID of the user making the request
     * @return The updated option DTO
     */
    @PutMapping("/{optionId}")
    public ResponseEntity<OptionDto> update(
            @PathVariable Long formId,
            @PathVariable Long questionId,
            @PathVariable Long optionId,
            @RequestBody OptionDto dto,
            @RequestHeader("X-User-ID") Long userId) {
        dto.setId(optionId);
        dto.setQuestionId(questionId);
        return ResponseEntity.ok(optionService.updateOption(optionId, dto, userId));
    }

    /**
     * Deletes an option.
     *
     * @param formId The ID of the form containing the question
     * @param questionId The ID of the question containing the option
     * @param optionId The ID of the option to delete
     * @param userId The ID of the user making the request
     * @return No content response
     */
    @DeleteMapping("/{optionId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long formId,
            @PathVariable Long questionId,
            @PathVariable Long optionId,
            @RequestHeader("X-User-ID") Long userId) {
        optionService.deleteOption(optionId, userId);
        return ResponseEntity.noContent().build();
    }
}