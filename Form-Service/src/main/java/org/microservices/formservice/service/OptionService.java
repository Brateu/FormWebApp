package org.microservices.formservice.service;

import org.microservices.formservice.DTO.OptionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OptionService {
    List<OptionDto> getOptionsByQuestion(Long questionId);
    OptionDto createOption(Long questionId, OptionDto dto);
    OptionDto updateOption(Long id, OptionDto dto);
    void deleteOption(Long id);
}