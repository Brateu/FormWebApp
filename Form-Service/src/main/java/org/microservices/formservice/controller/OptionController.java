package org.microservices.formservice.controller;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.OptionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.microservices.formservice.service.OptionService;

import java.util.List;

@RestController
@RequestMapping("/api/questions/{questionId}/options")
@RequiredArgsConstructor
public class OptionController {

    private final OptionService optionService;

    @GetMapping
    public ResponseEntity<List<OptionDto>> getOptions(@PathVariable Long questionId) {
        return ResponseEntity.ok(optionService.getOptionsByQuestion(questionId));
    }

    @PostMapping
    public ResponseEntity<OptionDto> create(@PathVariable Long questionId, @RequestBody OptionDto dto) {
        return ResponseEntity.ok(optionService.createOption(questionId, dto));
    }

    @PutMapping("/{optionId}")
    public ResponseEntity<OptionDto> update(@PathVariable Long optionId, @RequestBody OptionDto dto) {
        return ResponseEntity.ok(optionService.updateOption(optionId, dto));
    }

    @DeleteMapping("/{optionId}")
    public ResponseEntity<Void> delete(@PathVariable Long optionId, @PathVariable String questionId) {
        optionService.deleteOption(optionId);
        return ResponseEntity.noContent().build();
    }
}