package org.microservices.formservice.service;


import jakarta.ws.rs.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.FormDto;
import org.microservices.formservice.entity.Form;
import org.microservices.formservice.enums.CollaboratorRole;
import org.microservices.formservice.enums.Status;
import org.microservices.formservice.enums.Visibility;
import org.microservices.formservice.mappers.FormMapper;
import org.microservices.formservice.repository.CollaboratorRepository;
import org.microservices.formservice.repository.FormRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FormServiceImpl implements FormService {

    private final FormRepository formRepo;
    private final CollaboratorRepository collabRepo;
    private final FormMapper formMapper;

    @Override
    public List<FormDto> getAllForms() {
        return formRepo.findAll()
                .stream()
                .map(formMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FormDto getFormById(Long id, Long userId) {
        Form form = formRepo.findById(id).orElseThrow(() -> new RuntimeException("Form not found with ID: " + id));
        if (form.getVisibility() == Visibility.PRIVATE) {
            if (userId == null || (!form.getCreatedBy().equals(userId) && !collabRepo.existsByFormIdAndUserId(id, userId))) {
                throw new NotAuthorizedException("You don't have permission to access this form");
            }
        }
            return formMapper.toDto(form);
    }

    @Override
    @Transactional
    public FormDto createForm(FormDto formDto) {
        Form form = formMapper.toEntity(formDto);
        form.setCreatedAt(LocalDateTime.now());
        form.setUpdatedAt(LocalDateTime.now());
        if (form.getStatus() == null) {
            form.setStatus(Status.DRAFT);
        }

        if (form.getVisibility() == null) {
            form.setVisibility(Visibility.PRIVATE);
        }

        form = formRepo.save(form);
        return formMapper.toDto(form);

    }

    @Override
    public List<FormDto> getUserForms(Long userId) {
        List<Form> ownedForms = formRepo.findByCreatedBy(userId);
        List<Form> collaboratedForms = formRepo.findByCollaborators_UserId(userId);
        ownedForms.addAll(collaboratedForms.stream()
                .filter(form -> ownedForms.stream()
                        .noneMatch(ownedForm -> ownedForm.getId().equals(form.getId())))
                .toList());
        return ownedForms.stream()
                .map(formMapper::toDto)
                .toList();
    }

    @Override
    public FormDto updateFormStatus(Long id, Status status, Long userId) {
        Form form = formRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found with ID: " + id));
        if (!form.getCreatedBy().equals(userId) &&
                !collabRepo.existsByFormIdAndUserIdAndRole(id, userId, CollaboratorRole.EDITOR)) {
            throw new NotAuthorizedException("You don't have permission to update this form");
        }
        form.setStatus(status);
        form.setUpdatedAt(LocalDateTime.now());
        form = formRepo.save(form);

        return formMapper.toDto(form);
    }

    @Override
    public FormDto updateFormVisibility(Long id, Visibility visibility, Long userId) {
        Form form = formRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found with ID: " + id));
        if (!form.getCreatedBy().equals(userId)) {
            throw new NotAuthorizedException("Only the form owner can change visibility settings");
        }
        form.setVisibility(visibility);
        form.setUpdatedAt(LocalDateTime.now());
        form = formRepo.save(form);

        return formMapper.toDto(form);
    }

    @Override
    public List<FormDto> getFormsByStatus(Status status) {
        return formRepo.findByStatus(status)
                .stream()
                .map(formMapper::toDto)
                .toList();
    }

    @Override
    public List<FormDto> getFormsByVisibility(Visibility visibility) {
        return formRepo.findByVisibility(visibility)
                .stream()
                .map(formMapper::toDto)
                .toList();
    }

    @Override
    public List<FormDto> getPublicForms() {
        return formRepo.getFormsByVisibility(Visibility.PUBLIC)
                .stream()
                .map(formMapper::toDto)
                .toList();
    }

    @Override
    public FormDto copyForm(Long id, Long userId) {
        Form originalForm = formRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found with ID: " + id));

        // Check if user can access the form to copy
        if (originalForm.getVisibility() == Visibility.PRIVATE &&
                !originalForm.getCreatedBy().equals(userId) &&
                !collabRepo.existsByFormIdAndUserId(id, userId)) {
            throw new NotAuthorizedException("You don't have permission to copy this form");
        }

        // Create a new form based on the original form
        Form newForm = new Form();
        newForm.setName(originalForm.getName() + " (Copy)");
        newForm.setDescription(originalForm.getDescription());
        newForm.setAllowAnonymous(originalForm.isAllowAnonymous());
        newForm.setStatus(Status.DRAFT);
        newForm.setVisibility(Visibility.PRIVATE);
        newForm.setCreatedBy(userId);
        newForm.setCreatedAt(LocalDateTime.now());
        newForm.setUpdatedAt(LocalDateTime.now());

        Form savedForm = formRepo.save(newForm);

        // TODO: Copy questions and their options

        return formMapper.toDto(savedForm);

    }

    @Override
    public FormDto lockForm(Long id, Long userId) {
        Form form = formRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found with ID: " + id));

        if (!form.getCreatedBy().equals(userId) &&
                !collabRepo.existsByFormIdAndUserIdAndRole(id, userId, CollaboratorRole.EDITOR)) {
            throw new NotAuthorizedException("You don't have permission to lock this form");
        }

        form.setLocked(true);
        form.setUpdatedAt(LocalDateTime.now());
        form = formRepo.save(form);

        return formMapper.toDto(form);

    }

    @Override
    public FormDto unlockForm(Long id, Long userId) {
        Form form = formRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found with ID: " + id));

        if (!form.getCreatedBy().equals(userId) &&
                !collabRepo.existsByFormIdAndUserIdAndRole(id, userId, CollaboratorRole.EDITOR)) {
            throw new NotAuthorizedException("You don't have permission to unlock this form");
        }

        form.setLocked(false);
        form.setUpdatedAt(LocalDateTime.now());
        form = formRepo.save(form);

        return formMapper.toDto(form);

    }

    @Override
    public FormDto updateForm(Long id, FormDto formDto) {
        Form form = formRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found with ID: " + id));

        Long userId = formDto.getCreatedBy();
        if (!form.getCreatedBy().equals(userId) &&
                !collabRepo.existsByFormIdAndUserIdAndRole(id, userId, CollaboratorRole.EDITOR)) {
            throw new NotAuthorizedException("You don't have permission to update this form");
        }

        form.setName(formDto.getName());
        form.setDescription(formDto.getDescription());
        form.setAllowAnonymous(formDto.isAllowAnonymous());
        form.setUpdatedAt(LocalDateTime.now());

        return formMapper.toDto(formRepo.save(form));

    }

    @Override
    public void deleteForm(Long id) {
        if (!formRepo.existsById(id)) {
      throw new RuntimeException("Form not found with ID: " + id);
        }
        collabRepo.deleteByFormId(id);
        formRepo.deleteById(id);
    }
}