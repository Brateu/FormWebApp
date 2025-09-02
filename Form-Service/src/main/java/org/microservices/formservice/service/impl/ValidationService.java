package org.microservices.formservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.entity.Form;
import org.microservices.formservice.enums.CollaboratorRole;
import org.microservices.formservice.enums.Visibility;
import org.microservices.formservice.repository.CollaboratorRepository;
import org.microservices.formservice.repository.FormRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service for handling authorization logic across the application.
 * This centralizes the authorization checks to avoid code duplication.
 */
@Service
@RequiredArgsConstructor
public class ValidationService {

    private final CollaboratorRepository collaboratorRepository;
    private final FormRepository formRepo;


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
     * Checks if a user is authorized to access collaborator information based on form ID and user ID.
     * This method checks authorization without fetching the entire form.
     * This is more restrictive than the standard authorization check.
     *
     * @param formId The ID of the form to check authorization for
     * @param userId The ID of the user attempting to access collaborator information
     * @return true if the user is authorized, false otherwise
     */
    @Cacheable(value = "userAuthorizedForCollaboratorsCache", key = "{#formId, #userId}")
    public boolean isUserAuthorizedForCollaborators(Long formId, Long userId) {
        if (formId == null || userId == null) {
            return false;
        }

        // Form owner is always authorized
        boolean isOwner = formRepo.findOwnerIdByFormId(formId)
                .map(ownerId -> ownerId.equals(userId))
                .orElse(false);
        if (isOwner) {
            return true;
        }

        // Editor collaborators are authorized
        boolean isEditorCollaborator = formRepo.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR);
        if (isEditorCollaborator) {
            return true;
        }

        // Viewer collaborators are authorized if the form is private
        boolean isViewerCollaborator = formRepo.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER);
        boolean isPrivate = formRepo.findVisibilityByFormId(formId)
                .map(visibility -> visibility == Visibility.PRIVATE)
                .orElse(false);

        if (isPrivate) {
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
    @Cacheable(value = "collaboratorCache", key = "{#formId, #userId}")
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
    @Cacheable(value = "collaboratorEditorCache", key = "{#formId, #userId}")
    public boolean isCollaboratorEditor(Long formId, Long userId) {
        return userId != null && collaboratorRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR);
    }

    /**
     * Checks if a user is a viewer collaborator on a form.
     *
     * @param formId The ID of the form to check
     * @param userId The ID of the user to check
     * @return true if the user is a viewer collaborator, false otherwise
     */
    @Cacheable(value = "collaboratorViewerCache", key = "{#formId, #userId}")
    public boolean isCollaboratorViewer(Long formId, Long userId) {
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

        return isCollaboratorEditor(form.getId(), userId);
    }

    /**
     * Checks if a user is authorized to access a form.
     * This includes form owners, collaborators, and users accessing public forms.
     *
     * @param form The form to check authorization for
     * @param userId The ID of the user attempting to access the form
     * @return true if the user is authorized, false otherwise
     */
    public boolean isUserAuthorized(Form form, Long userId) {
        if (userId == null) {
            return false;
        }

        // Form owner is always authorized
        boolean isOwner = form.getCreatedBy().equals(userId);
        if (isOwner) {
            return true;
        }

        // Editor collaborators are authorized
        boolean isEditorCollaborator = isCollaboratorEditor(form.getId(), userId);
        if (isEditorCollaborator) {
            return true;
        }

        // Viewer collaborators are authorized
        boolean isViewerCollaborator = isCollaboratorViewer(form.getId(), userId);
        if (isViewerCollaborator) {
            return true;
        }

        // For public forms, anyone is authorized
        if (form.getVisibility() == Visibility.PUBLIC) {
            return true;
        }

        // For forms that allow anonymous access, anyone is authorized
        return form.isAllowAnonymous();
    }

    /**
     * Checks if a user is authorized to access a form based on form ID and user ID.
     * This method checks all authorization types without fetching the entire form.
     *
     * @param formId The ID of the form to check authorization for
     * @param userId The ID of the user attempting to access the form
     * @return true if the user is authorized, false otherwise
     */
    @Cacheable(value = "userAuthorizedCache", key = "{#formId, #userId}")
    public boolean isUserAuthorized(Long formId, Long userId) {
        if (formId == null || userId == null) {
            return false;
        }

        // Form owner is always authorized
        boolean isOwner = formRepo.findOwnerIdByFormId(formId)
                .map(ownerId -> ownerId.equals(userId))
                .orElse(false);
        if (isOwner) {
            return true;
        }

        // Editor collaborators are authorized
        boolean isEditorCollaborator = formRepo.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR);
        if (isEditorCollaborator) {
            return true;
        }

        // Viewer collaborators are authorized
        boolean isViewerCollaborator = formRepo.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER);
        if (isViewerCollaborator) {
            return true;
        }

        // For public forms, anyone is authorized
        boolean isPublic = formRepo.findVisibilityByFormId(formId)
                .map(visibility -> visibility == Visibility.PUBLIC)
                .orElse(false);
        if (isPublic) {
            return true;
        }

        // For forms that allow anonymous access, anyone is authorized
        return formRepo.findAllowAnonymousByFormId(formId)
                .orElse(false);
    }

    /**
     * @deprecated Use {@link #isUserAuthorized(Long, Long)} instead.
     */
    @Deprecated
    public boolean isUserAuthorised(Long formId, Long userId) {
        return isUserAuthorized(formId, userId);
    }


    /**
     * Checks if a user is authorized to edit a form based on form ID and user ID.
     * This method checks authorization without fetching the entire form.
     *
     * @param formId The ID of the form to check authorization for
     * @param userId The ID of the user attempting to edit the form
     * @return true if the user is authorized to edit, false otherwise
     */
    @Cacheable(value = "userAuthorizedToEditCache", key = "{#formId, #userId}")
    public boolean isUserAuthorizedToEdit(Long formId, Long userId) {
        if(formId == null || userId == null) {
            return false;
        }

        // Form owner is always authorized to edit
        boolean isOwner = formRepo.findOwnerIdByFormId(formId)
                .map(ownerId -> ownerId.equals(userId))
                .orElse(false);
        if (isOwner) {
            return true;
        }

        // Editor collaborators are authorized to edit
        return formRepo.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR);
    }

    /**
     * Checks if a user is the owner of a form based on form ID and user ID.
     * This method checks ownership without fetching the entire form.
     *
     * @param formId The ID of the form to check ownership for
     * @param userId The ID of the user to check
     * @return true if the user is the owner, false otherwise
     */
    @Cacheable(value = "formOwnerCache", key = "{#formId, #userId}")
    public boolean isFormOwner(Long formId, Long userId) {
        if(formId == null || userId == null) {
            return false;
        }

        return formRepo.findOwnerIdByFormId(formId)
                .map(ownerId -> ownerId.equals(userId))
                .orElse(false);
    }
}