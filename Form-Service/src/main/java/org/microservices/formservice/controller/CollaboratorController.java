package org.microservices.formservice.controller;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.CollaboratorDto;
import org.microservices.formservice.service.CollaboratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing collaborators on forms.
 * Provides endpoints for retrieving, adding, updating, and removing collaborators.
 */
@RestController
@RequestMapping("/api/forms/{formId}/collaborators")
@RequiredArgsConstructor
public class CollaboratorController {

    private final CollaboratorService collaboratorService;

    /**
     * Retrieves all collaborators for a form.
     * 
     * @param formId The ID of the form
     * @param userId The ID of the user making the request
     * @return A list of collaborator DTOs
     */
    @GetMapping
    public ResponseEntity<List<CollaboratorDto>> getAll(
            @PathVariable Long formId,
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(collaboratorService.getCollaborators(formId, userId));
    }

    /**
     * Adds a new collaborator to a form.
     * 
     * @param formId The ID of the form
     * @param dto The collaborator data
     * @param userId The ID of the user making the request
     * @return The created collaborator DTO
     */
    @PostMapping
    public ResponseEntity<CollaboratorDto> add(
            @PathVariable Long formId,
            @RequestBody CollaboratorDto dto,
            @RequestHeader("X-User-ID") Long userId) {
        dto.setFormId(formId);
        return ResponseEntity.ok(collaboratorService.addCollaborator(formId, dto, userId));
    }

    /**
     * Updates an existing collaborator on a form.
     * 
     * @param formId The ID of the form
     * @param collaboratorId The ID of the collaborator to update
     * @param dto The updated collaborator data
     * @param userId The ID of the user making the request
     * @return The updated collaborator DTO
     */
    @PutMapping("/{collaboratorId}")
    public ResponseEntity<CollaboratorDto> update(
            @PathVariable Long formId,
            @PathVariable Long collaboratorId,
            @RequestBody CollaboratorDto dto,
            @RequestHeader("X-User-ID") Long userId) {
        dto.setId(collaboratorId);
        dto.setFormId(formId);
        return ResponseEntity.ok(collaboratorService.updateCollaborator(formId, dto, userId));
    }

    /**
     * Removes a collaborator from a form.
     * 
     * @param collaboratorId The ID of the collaborator to remove
     * @param formId The ID of the form
     * @param userId The ID of the user making the request
     * @return No content response
     */
    @DeleteMapping("/{collaboratorId}")
    public ResponseEntity<Void> remove(
            @PathVariable Long collaboratorId,
            @PathVariable Long formId,
            @RequestHeader("X-User-ID") Long userId) {
        collaboratorService.removeCollaborator(collaboratorId, userId);
        return ResponseEntity.noContent().build();
    }
}