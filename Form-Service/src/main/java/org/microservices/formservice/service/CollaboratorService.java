package org.microservices.formservice.service;

import org.microservices.formservice.DTO.CollaboratorDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service interface for managing collaborators on forms.
 */
@Service
public interface CollaboratorService {
    /**
     * Retrieves all collaborators for a form.
     * 
     * @param formId The ID of the form
     * @param userId The ID of the user making the request
     * @return A list of collaborator DTOs
     */
    List<CollaboratorDto> getCollaborators(Long formId, Long userId);

    /**
     * Adds a new collaborator to a form.
     * 
     * @param formId The ID of the form
     * @param dto The collaborator data
     * @param userId The ID of the user making the request
     * @return The created collaborator DTO
     */
    CollaboratorDto addCollaborator(Long formId, CollaboratorDto dto, Long userId);

    /**
     * Updates an existing collaborator on a form.
     * 
     * @param formId The ID of the form
     * @param dto The collaborator data with updated information
     * @param userId The ID of the user making the request
     * @return The updated collaborator DTO
     */
    CollaboratorDto updateCollaborator(Long formId, CollaboratorDto dto, Long userId);

    /**
     * Removes a collaborator from a form.
     * 
     * @param id The ID of the collaborator to remove
     * @param userId The ID of the user making the request
     */
    void removeCollaborator(Long id, Long userId);
}