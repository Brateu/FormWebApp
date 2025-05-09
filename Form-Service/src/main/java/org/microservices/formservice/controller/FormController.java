package org.microservices.formservice.controller;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.FormDto;
import org.microservices.formservice.service.FormService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.microservices.formservice.enums.Status;
import org.microservices.formservice.enums.Visibility;
import java.util.List;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    @GetMapping
    public ResponseEntity<List<FormDto>> getAllForms() {
        return ResponseEntity.ok(formService.getAllForms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormDto> getFormById(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.getFormById(id, userId));
    }

    @PostMapping
    public ResponseEntity<FormDto> createForm(@RequestBody FormDto formDto, @RequestHeader("X-User-ID") Long userId) {
        formDto.setCreatedBy(userId);
        return ResponseEntity.ok(formService.createForm(formDto));
    }

    @GetMapping("/user")
    public ResponseEntity<List<FormDto>> getUserForms(@RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.getUserForms(userId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<FormDto> updateFormStatus(
            @PathVariable Long id,
            @RequestParam Status status,
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.updateFormStatus(id, status, userId));
    }

    @PutMapping("/{id}/visibility")
    public ResponseEntity<FormDto> updateFormVisibility(
            @PathVariable Long id,
            @RequestParam Visibility visibility,
            @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.updateFormVisibility(id, visibility, userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FormDto>> getFormsByStatus(@PathVariable Status status) {
        return ResponseEntity.ok(formService.getFormsByStatus(status));
    }

    @GetMapping("/visibility/{visibility}")
    public ResponseEntity<List<FormDto>> getFormsByVisibility(@PathVariable Visibility visibility) {
        return ResponseEntity.ok(formService.getFormsByVisibility(visibility));
    }

    @GetMapping("/public")
    public ResponseEntity<List<FormDto>> getPublicForms() {
        return ResponseEntity.ok(formService.getPublicForms());
    }

    @PostMapping("/{id}/copy")
    public ResponseEntity<FormDto> copyForm(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.copyForm(id, userId));
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<FormDto> lockForm(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.lockForm(id, userId));
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<FormDto> unlockForm(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(formService.unlockForm(id, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormDto> updateForm(
            @PathVariable Long id,
            @RequestBody FormDto formDto) {
        return ResponseEntity.ok(formService.updateForm(id, formDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id, @RequestHeader("X-User-ID") Long userId) {
        formService.deleteForm(id);
        return ResponseEntity.noContent().build();
    }

}