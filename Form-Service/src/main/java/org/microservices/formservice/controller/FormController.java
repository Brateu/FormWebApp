package org.microservices.formservice.controller;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.FormDto;
import org.microservices.formservice.service.FormService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.microservices.formservice.enums.Status;
import org.microservices.formservice.enums.Visibility;
import java.util.List;

/**
 * REST controller for managing forms.
 * Provides endpoints for creating, retrieving, updating, and deleting forms,
 * as well as managing form status, visibility, and other operations.
 */
@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormController {

    /**
     * Service for form-related operations.
     */
    private final FormService formService;

    /**
     * Retrieves all forms in the system.
     *
     * @return A list of all form DTOs
     */
    @GetMapping
    public ResponseEntity<List<FormDto>> getAllForms() {
        return ResponseEntity.ok(formService.getAllForms());
    }

    /**
     * Retrieves a specific form by its ID.
     *
     * @param id The ID of the form to retrieve
     * @param userId The ID of the user making the request
     * @return The form DTO if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<FormDto> getFormById(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.getFormById(id, userId));
    }

    /**
     * Creates a new form.
     *
     * @param formDto The form data to create
     * @param userId The ID of the user creating the form
     * @return The created form DTO
     */
    @PostMapping
    public ResponseEntity<FormDto> createForm(@RequestBody FormDto formDto, @RequestHeader("X-User-ID") Long userId) {
        formDto.setCreatedBy(userId);
        return ResponseEntity.ok(formService.createForm(formDto));
    }

    /**
     * Retrieves all forms created by or shared with the current user.
     *
     * @param userId The ID of the user making the request
     * @return A list of form DTOs
     */
    @GetMapping("/user")
    public ResponseEntity<List<FormDto>> getUserForms(@RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.getUserForms(userId));
    }

    /**
     * Updates the status of a form.
     *
     * @param id The ID of the form to update
     * @param status The new status value
     * @param userId The ID of the user making the request
     * @return The updated form DTO
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<FormDto> updateFormStatus(
            @PathVariable Long id,
            @RequestParam Status status,
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.updateFormStatus(id, status, userId));
    }

    /**
     * Updates the visibility of a form.
     *
     * @param id The ID of the form to update
     * @param visibility The new visibility value
     * @param userId The ID of the user making the request
     * @return The updated form DTO
     */
    @PutMapping("/{id}/visibility")
    public ResponseEntity<FormDto> updateFormVisibility(
            @PathVariable Long id,
            @RequestParam Visibility visibility,
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.updateFormVisibility(id, visibility, userId));
    }

    /**
     * Retrieves all forms with a specific status.
     *
     * @param status The status to filter by
     * @return A list of form DTOs with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<FormDto>> getFormsByStatus(@PathVariable Status status) {
        return ResponseEntity.ok(formService.getFormsByStatus(status));
    }

    /**
     * Retrieves all forms with a specific visibility.
     *
     * @param visibility The visibility to filter by
     * @return A list of form DTOs with the specified visibility
     */
    @GetMapping("/visibility/{visibility}")
    public ResponseEntity<List<FormDto>> getFormsByVisibility(@PathVariable Visibility visibility) {
        return ResponseEntity.ok(formService.getFormsByVisibility(visibility));
    }

    /**
     * Retrieves all forms with public visibility.
     *
     * @return A list of public form DTOs
     */
    @GetMapping("/public")
    public ResponseEntity<List<FormDto>> getPublicForms() {
        return ResponseEntity.ok(formService.getPublicForms());
    }

    /**
     * Retrieves a specific public form by its ID.
     *
     * @param id The ID of the public form to retrieve
     * @return The public form DTO if found
     */
    @GetMapping("/public/{id}")
    public ResponseEntity<FormDto> getPublicFormById(@PathVariable Long id) {
        return ResponseEntity.ok(formService.getPublicFormById(id));
    }

    /**
     * Creates a copy of an existing form.
     *
     * @param id The ID of the form to copy
     * @param userId The ID of the user making the copy
     * @return The newly created form copy DTO
     */
    @PostMapping("/{id}/copy")
    public ResponseEntity<FormDto> copyForm(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.copyForm(id, userId));
    }

    /**
     * Locks a form to prevent further editing.
     *
     * @param id The ID of the form to lock
     * @param userId The ID of the user making the request
     * @return The updated form DTO
     */
    @PutMapping("/{id}/lock")
    public ResponseEntity<FormDto> lockForm(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.lockForm(id, userId));
    }

    /**
     * Unlocks a form to allow editing.
     *
     * @param id The ID of the form to unlock
     * @param userId The ID of the user making the request
     * @return The updated form DTO
     */
    @PutMapping("/{id}/unlock")
    public ResponseEntity<FormDto> unlockForm(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.unlockForm(id, userId));
    }

    /**
     * Updates an existing form.
     *
     * @param id The ID of the form to update
     * @param formDto The updated form data
     * @param userId The ID of the user making the request
     * @return The updated form DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<FormDto> updateForm(
            @PathVariable Long id,
            @RequestBody FormDto formDto,
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.updateForm(id, formDto, userId));
    }

    /**
     * Deletes a form.
     *
     * @param id The ID of the form to delete
     * @param userId The ID of the user making the request
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
        formService.deleteForm(id, userId);
        return ResponseEntity.noContent().build();
    }

}