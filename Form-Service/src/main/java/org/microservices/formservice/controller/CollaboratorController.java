package org.microservices.formservice.controller;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.CollaboratorDto;
import org.microservices.formservice.service.CollaboratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms/{formId}/collaborators")
@RequiredArgsConstructor
public class CollaboratorController {

    private final CollaboratorService collaboratorService;

    @GetMapping
    public ResponseEntity<List<CollaboratorDto>> getAll(@PathVariable Long formId) {
        return ResponseEntity.ok(collaboratorService.getCollaborators(formId));
    }

    @PostMapping
    public ResponseEntity<CollaboratorDto> add(@PathVariable Long formId, @RequestBody CollaboratorDto dto) {
        return ResponseEntity.ok(collaboratorService.addCollaborator(formId, dto));
    }

    @DeleteMapping("/{collaboratorId}")
    public ResponseEntity<Void> remove(@PathVariable Long collaboratorId, @PathVariable String formId) {
        collaboratorService.removeCollaborator(collaboratorId);
        return ResponseEntity.noContent().build();
    }
}