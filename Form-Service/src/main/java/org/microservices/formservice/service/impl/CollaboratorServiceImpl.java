package org.microservices.formservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microservices.formservice.DTO.CollaboratorDto;
import org.microservices.formservice.client.UserService;
import org.microservices.formservice.exception.ResourceNotFoundException;
import org.microservices.formservice.exception.UnauthorizedException;
import org.microservices.formservice.exception.UserNotFoundException;
import org.microservices.formservice.service.CollaboratorService;
import org.springframework.stereotype.Service;
import org.microservices.formservice.repository.CollaboratorRepository;
import org.microservices.formservice.repository.FormRepository;
import org.microservices.formservice.mappers.CollaboratorMapper;
import org.microservices.formservice.entity.Collaborator;
import org.microservices.formservice.entity.Form;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the CollaboratorService interface.
 * Provides functionality for managing collaborators on forms.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CollaboratorServiceImpl implements CollaboratorService {
    private final CollaboratorRepository collaboratorRepository;
    private final FormRepository formRepository;
    private final CollaboratorMapper collaboratorMapper;
    private final ValidationService validationService;
    private final UserService userService;

    /**
     * Retrieves a list of collaborators associated with the specified form.
     *
     * @param formId The unique identifier of the form whose collaborators are to be retrieved.
     * @param userId The unique identifier of the user requesting the collaborators.
     * @return A list of CollaboratorDto objects representing the collaborators of the specified form.
     * @throws ResourceNotFoundException if the form with the specified formId is not found.
     * @throws UnauthorizedException if the user is not authorized to view collaborators for the specified form.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CollaboratorDto> getCollaborators(Long formId, Long userId) {
        if (!formRepository.existsById(formId)) {
            throw new ResourceNotFoundException("Form not found with id: " + formId);
        }

        if (!validationService.isUserAuthorizedForCollaborators(formId, userId)) {
            throw new UnauthorizedException("User is not authorized to view collaborators for this form");
        }

        return collaboratorRepository.findByFormId(formId)
                .stream()
                .map(collaboratorMapper::toDto)
                .toList();
    }

    /**
     * Adds a collaborator to a specific form. The form owner must authorize
     * this action. If the collaborator's user ID is not provided, their email
     * is used to identify the corresponding user in the system.
     *
     * @param formId The ID of the form to which the collaborator is being added.
     * @param dto The data transfer object containing collaborator information.
     * @param userId The ID of the user performing the action; must be the form owner.
     * @return The data transfer object representing the added collaborator.
     * @throws ResourceNotFoundException If the form or the user referenced by the email is not found.
     * @throws UnauthorizedException If the user performing the action is not the form owner.
     * @throws IllegalArgumentException If the collaborator email is not provided when the user ID is null.
     */
    @Override
    @Transactional
    public CollaboratorDto addCollaborator(Long formId, CollaboratorDto dto, Long userId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + formId));

        if (!form.getCreatedBy().equals(userId)) {
            throw new UnauthorizedException("Only the form owner can add collaborators");
        }

        Collaborator collaborator = collaboratorMapper.toEntity(dto);
        collaborator.setForm(form);

        if (collaborator.getUserId() == null) {
            if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
                throw new IllegalArgumentException("Collaborator email must be provided");
            }

            try {
                log.info("Looking up user ID for email: {}", dto.getEmail());
                Long collaboratorUserId = userService.getUserIdByEmail(dto.getEmail());

                if (collaboratorUserId == null) {
                    log.error("User not found with email: {}", dto.getEmail());
                    throw new UserNotFoundException(dto.getEmail());
                }

                log.info("Found user ID: {} for email: {}", collaboratorUserId, dto.getEmail());
                collaborator.setUserId(collaboratorUserId);
            } catch (UserNotFoundException e) {
                log.error("User not found with email: {}", dto.getEmail());
                throw new ResourceNotFoundException("User not found with email: " + dto.getEmail(), e);
            } catch (Exception e) {
                log.error("Error looking up user with email: {}", dto.getEmail(), e);
                throw new ResourceNotFoundException("Error looking up user with email: " + dto.getEmail(), e);
            }
        }

        return collaboratorMapper.toDto(collaboratorRepository.save(collaborator));
    }

    /**
     * Updates an existing collaborator for a given form.
     *
     * @param formId the ID of the form to which the collaborator belongs
     * @param dto the collaborator data transfer object containing updated information
     * @param userId the ID of the user performing the update
     * @return the updated collaborator data transfer object
     * @throws ResourceNotFoundException if the form or collaborator is not found
     * @throws UnauthorizedException if the user is not authorized to update the collaborator
     */
    @Override
    @Transactional
    public CollaboratorDto updateCollaborator(Long formId, CollaboratorDto dto, Long userId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + formId));

        if (!form.getCreatedBy().equals(userId)) {
            throw new UnauthorizedException("Only the form owner can update collaborators");
        }

        Collaborator existingCollaborator = collaboratorRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found with id: " + dto.getId()));

        existingCollaborator.setRole(dto.getRole());

        return collaboratorMapper.toDto(collaboratorRepository.save(existingCollaborator));
    }

    /**
     * Removes a collaborator identified by the given ID from the system.
     * This operation is only allowed if the requester is the owner of the form
     * associated with the collaborator.
     *
     * @param id the unique identifier of the collaborator to be removed
     * @param userId the unique identifier of the user attempting to remove the collaborator
     * @throws ResourceNotFoundException if no collaborator is found with the given ID
     * @throws UnauthorizedException if the user is not the owner of the form associated with the collaborator
     */
    @Override
    @Transactional
    public void removeCollaborator(Long id, Long userId) {
        Collaborator collaborator = collaboratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collaborator not found with id: " + id));

        if (!collaborator.getForm().getCreatedBy().equals(userId)) {
            throw new UnauthorizedException("Only the form owner can remove collaborators");
        }

        collaboratorRepository.deleteById(id);
    }

    private boolean isUserAuthorized(Form form, Long userId) {
        return validationService.isUserAuthorizedForCollaborators(form, userId);
    }
}