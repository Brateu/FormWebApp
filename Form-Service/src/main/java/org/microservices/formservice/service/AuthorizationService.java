package org.microservices.formservice.service;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.entity.Form;
import org.microservices.formservice.enums.CollaboratorRole;
import org.microservices.formservice.enums.Visibility;
import org.microservices.formservice.repository.CollaboratorRepository;
import org.springframework.stereotype.Service;

/**
 * Service for handling authorization logic across the application.
 * This centralizes the authorization checks to avoid code duplication.
 */
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final CollaboratorRepository collaboratorRepository;

    /**
     * Checks if a user is authorized to access a form and its resources.
     * This is the standard authorization check used for most operations.
     *
     * @param form The form to check authorization for
     * @param userId The ID of the user attempting to access the form
     * @return true if the user is authorized, false otherwise
     */
    public boolean isUserAuthorized(Form form, Long userId) {
        if (form.isAllowAnonymous()) {
            return true;
        }

        if (userId == null) {
            return false;
        }

        boolean isOwner = form.getCreatedBy().equals(userId);

        if (isOwner) {
            return true;
        }

        boolean isEditorCollaborator = collaboratorRepository.existsByFormIdAndUserIdAndRole(form.getId(), userId, CollaboratorRole.EDITOR);
        if (isEditorCollaborator) {
            return true;
        }

        boolean isViewerCollaborator = collaboratorRepository.existsByFormIdAndUserIdAndRole(form.getId(), userId, CollaboratorRole.VIEWER);

        if (form.getVisibility() == Visibility.PRIVATE) {
            return isViewerCollaborator;
        }

        return true;
    }

    /**
     * Checks if a user is authorized to access collaborator information.
     * This is more restrictive than the standard authorization check.
     *
     * @param form The form to check authorization for
     * @param userId The ID of the user attempting to access collaborator information
     * @return true if the user is authorized, false otherwise
     */
    public boolean isUserAuthorizedForCollaborators(Form form, Long userId) {
        if (userId == null) {
            return false;
        }

        boolean isOwner = form.getCreatedBy().equals(userId);

        if (isOwner) {
            return true;
        }

        boolean isEditorCollaborator = collaboratorRepository.existsByFormIdAndUserIdAndRole(form.getId(), userId, CollaboratorRole.EDITOR);
        if (isEditorCollaborator) {
            return true;
        }

        boolean isViewerCollaborator = collaboratorRepository.existsByFormIdAndUserIdAndRole(form.getId(), userId, CollaboratorRole.VIEWER);

        if (form.getVisibility() == Visibility.PRIVATE) {
            return isViewerCollaborator;
        }

        return true;
    }

    /**
     * Checks if a user is the owner of a form.
     *
     * @param form The form to check ownership for
     * @param userId The ID of the user to check
     * @return true if the user is the owner, false otherwise
     */
    public boolean isFormOwner(Form form, Long userId) {
        return userId != null && form.getCreatedBy().equals(userId);
    }

    /**
     * Checks if a user is a collaborator on a form.
     *
     * @param formId The ID of the form to check
     * @param userId The ID of the user to check
     * @return true if the user is a collaborator, false otherwise
     */
    public boolean isCollaborator(Long formId, Long userId) {
        return userId != null && collaboratorRepository.existsByFormIdAndUserId(formId, userId);
    }

    /**
     * Checks if a user is an editor collaborator on a form.
     *
     * @param formId The ID of the form to check
     * @param userId The ID of the user to check
     * @return true if the user is an editor collaborator, false otherwise
     */
    public boolean isEditorCollaborator(Long formId, Long userId) {
        return userId != null && collaboratorRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR);
    }

    /**
     * Checks if a user is a viewer collaborator on a form.
     *
     * @param formId The ID of the form to check
     * @param userId The ID of the user to check
     * @return true if the user is a viewer collaborator, false otherwise
     */
    public boolean isViewerCollaborator(Long formId, Long userId) {
        return userId != null && collaboratorRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER);
    }

    /**
     * Checks if a user is authorized to edit a form and its resources.
     * Only the form owner and editor collaborators are allowed to edit.
     *
     * @param form The form to check authorization for
     * @param userId The ID of the user attempting to edit the form
     * @return true if the user is authorized to edit, false otherwise
     */
    public boolean isUserAuthorizedToEdit(Form form, Long userId) {
        if (userId == null) {
            return false;
        }

        boolean isOwner = form.getCreatedBy().equals(userId);
        if (isOwner) {
            return true;
        }

        return isEditorCollaborator(form.getId(), userId);
    }
}