package org.microservices.formservice.service;

import org.microservices.formservice.DTO.FormDto;
import org.microservices.formservice.enums.Visibility;
import org.microservices.formservice.enums.Status;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface FormService {
    List<FormDto> getAllForms();
    FormDto getFormById(Long id, Long userId);
    FormDto createForm(FormDto formDto);
    List<FormDto> getUserForms(Long userId);
    FormDto updateFormStatus(Long id, Status status, Long userId);
    FormDto updateFormVisibility(Long id, Visibility visibility, Long userId);
    List<FormDto> getFormsByStatus(Status status);
    List<FormDto> getFormsByVisibility(Visibility visibility);
    List<FormDto> getPublicForms();
    FormDto copyForm(Long id, Long userId);
    FormDto lockForm(Long id, Long userId);
    FormDto unlockForm(Long id, Long userId);
    FormDto updateForm(Long id, FormDto formDto);
    void deleteForm(Long id);
}