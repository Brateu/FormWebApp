package org.microservices.formservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.microservices.formservice.entity.Form;
import org.microservices.formservice.enums.CollaboratorRole;
import org.microservices.formservice.enums.Visibility;
import org.microservices.formservice.repository.CollaboratorRepository;
import org.microservices.formservice.repository.FormRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidationServiceTest {

    @Mock
    private FormRepository formRepository;

    @Mock
    private CollaboratorRepository collaboratorRepository;

    @InjectMocks
    private ValidationService validationService;

    private Form form;
    private Long userId;
    private Long formId;

    @BeforeEach
    void setUp() {
        userId = 123L;
        formId = 456L;

        form = new Form();
        form.setId(formId);
        form.setCreatedBy(userId);
        form.setAllowAnonymous(false);
        form.setVisibility(Visibility.PRIVATE);
    }

    @Test
    void testIsUserAuthorised_NullFormId() {
        // Test with null formId
        assertFalse(validationService.isUserAuthorised(null, userId));
    }

    @Test
    void testIsUserAuthorised_NullUserId() {
        // Test with null userId
        assertFalse(validationService.isUserAuthorised(formId, null));
    }

    @Test
    void testIsUserAuthorised_FormNotFound() {
        // Setup
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.empty());
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(false);
        when(formRepository.findVisibilityByFormId(formId)).thenReturn(Optional.empty());
        when(formRepository.findAllowAnonymousByFormId(formId)).thenReturn(Optional.empty());

        // Execute and verify
        assertFalse(validationService.isUserAuthorised(formId, userId));
    }

    @Test
    void testIsUserAuthorised_UserIsOwner() {
        // Setup
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(userId));

        // Execute and verify
        assertTrue(validationService.isUserAuthorised(formId, userId));
    }

    @Test
    void testIsUserAuthorised_UserIsEditorCollaborator() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(true);

        // Execute and verify
        assertTrue(validationService.isUserAuthorised(formId, userId));
    }

    @Test
    void testIsUserAuthorised_UserIsViewerCollaborator_PrivateForm() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(true);
        // No need to stub findVisibilityByFormId since the method returns early for viewer collaborators

        // Execute and verify
        assertTrue(validationService.isUserAuthorised(formId, userId));
    }

    @Test
    void testIsUserAuthorised_UserIsNotAuthorized_PrivateForm() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(false);
        when(formRepository.findVisibilityByFormId(formId)).thenReturn(Optional.of(Visibility.PRIVATE));
        when(formRepository.findAllowAnonymousByFormId(formId)).thenReturn(Optional.of(false));

        // Execute and verify
        assertFalse(validationService.isUserAuthorised(formId, userId));
    }

    @Test
    void testIsUserAuthorised_PublicForm() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(false);
        when(formRepository.findVisibilityByFormId(formId)).thenReturn(Optional.of(Visibility.PUBLIC));

        // Execute and verify
        assertTrue(validationService.isUserAuthorised(formId, userId));
    }

    @Test
    void testIsUserAuthorised_AllowAnonymous() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(false);
        when(formRepository.findVisibilityByFormId(formId)).thenReturn(Optional.of(Visibility.PRIVATE));
        when(formRepository.findAllowAnonymousByFormId(formId)).thenReturn(Optional.of(true));

        // Execute and verify
        assertTrue(validationService.isUserAuthorised(formId, userId));
    }

    @Test
    void testIsUserAuthorizedToEdit_NullFormId() {
        // Test with null formId
        assertFalse(validationService.isUserAuthorizedToEdit((Long)null, userId));
    }

    @Test
    void testIsUserAuthorizedToEdit_NullUserId() {
        // Test with null userId
        assertFalse(validationService.isUserAuthorizedToEdit(formId, null));
    }

    @Test
    void testIsUserAuthorizedToEdit_FormNotFound() {
        // Setup
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.empty());
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);

        // Execute and verify
        assertFalse(validationService.isUserAuthorizedToEdit(formId, userId));
    }

    @Test
    void testIsUserAuthorizedToEdit_UserIsOwner() {
        // Setup
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(userId));

        // Execute and verify
        assertTrue(validationService.isUserAuthorizedToEdit(formId, userId));
    }

    @Test
    void testIsUserAuthorizedToEdit_UserIsEditorCollaborator() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(true);

        // Execute and verify
        assertTrue(validationService.isUserAuthorizedToEdit(formId, userId));
    }

    @Test
    void testIsUserAuthorizedToEdit_UserIsNotAuthorized() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);

        // Execute and verify
        assertFalse(validationService.isUserAuthorizedToEdit(formId, userId));
    }

    @Test
    void testIsFormOwner_NullFormId() {
        // Test with null formId
        assertFalse(validationService.isFormOwner((Long)null, userId));
    }

    @Test
    void testIsFormOwner_NullUserId() {
        // Test with null userId
        assertFalse(validationService.isFormOwner(formId, null));
    }

    @Test
    void testIsFormOwner_FormNotFound() {
        // Setup
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.empty());

        // Execute and verify
        assertFalse(validationService.isFormOwner(formId, userId));
    }

    @Test
    void testIsFormOwner_UserIsOwner() {
        // Setup
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(userId));

        // Execute and verify
        assertTrue(validationService.isFormOwner(formId, userId));
    }

    @Test
    void testIsFormOwner_UserIsNotOwner() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));

        // Execute and verify
        assertFalse(validationService.isFormOwner(formId, userId));
    }

    @Test
    void testIsFormOwnerById_NullFormId() {
        // Test with null formId
        assertFalse(validationService.isFormOwner((Long)null, userId));
    }

    @Test
    void testIsFormOwnerById_NullUserId() {
        // Test with null userId
        assertFalse(validationService.isFormOwner(formId, null));
    }

    @Test
    void testIsFormOwnerById_UserIsOwner() {
        // Setup
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(userId));

        // Execute and verify
        assertTrue(validationService.isFormOwner(formId, userId));
    }

    @Test
    void testIsFormOwnerById_UserIsNotOwner() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));

        // Execute and verify
        assertFalse(validationService.isFormOwner(formId, userId));
    }

    @Test
    void testIsCollaborator_UserIsCollaborator() {
        // Setup
        when(collaboratorRepository.existsByFormIdAndUserId(formId, userId)).thenReturn(true);

        // Execute and verify
        assertTrue(validationService.isCollaborator(formId, userId));
    }

    @Test
    void testIsCollaborator_UserIsNotCollaborator() {
        // Setup
        when(collaboratorRepository.existsByFormIdAndUserId(formId, userId)).thenReturn(false);

        // Execute and verify
        assertFalse(validationService.isCollaborator(formId, userId));
    }

    @Test
    void testIsCollaboratorEditor_UserIsEditorCollaborator() {
        // Setup
        when(collaboratorRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(true);

        // Execute and verify
        assertTrue(validationService.isCollaboratorEditor(formId, userId));
    }

    @Test
    void testIsCollaboratorEditor_UserIsNotEditorCollaborator() {
        // Setup
        when(collaboratorRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);

        // Execute and verify
        assertFalse(validationService.isCollaboratorEditor(formId, userId));
    }

    @Test
    void testIsCollaboratorViewer_UserIsViewerCollaborator() {
        // Setup
        when(collaboratorRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(true);

        // Execute and verify
        assertTrue(validationService.isCollaboratorViewer(formId, userId));
    }

    @Test
    void testIsCollaboratorViewer_UserIsNotViewerCollaborator() {
        // Setup
        when(collaboratorRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(false);

        // Execute and verify
        assertFalse(validationService.isCollaboratorViewer(formId, userId));
    }

    @Test
    void testIsUserAuthorizedById_NullFormId() {
        // Test with null formId
        assertFalse(validationService.isUserAuthorized((Long)null, userId));
    }

    @Test
    void testIsUserAuthorizedById_NullUserId() {
        // Test with null userId
        assertFalse(validationService.isUserAuthorized(formId, null));
    }

    @Test
    void testIsUserAuthorizedById_UserIsOwner() {
        // Setup
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(userId));

        // Execute and verify
        assertTrue(validationService.isUserAuthorized(formId, userId));
    }

    @Test
    void testIsUserAuthorizedById_UserIsEditorCollaborator() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(true);

        // Execute and verify
        assertTrue(validationService.isUserAuthorized(formId, userId));
    }

    @Test
    void testIsUserAuthorizedById_UserIsViewerCollaborator() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(true);

        // Execute and verify
        assertTrue(validationService.isUserAuthorized(formId, userId));
    }

    @Test
    void testIsUserAuthorizedById_PublicForm() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(false);
        when(formRepository.findVisibilityByFormId(formId)).thenReturn(Optional.of(Visibility.PUBLIC));

        // Execute and verify
        assertTrue(validationService.isUserAuthorized(formId, userId));
    }

    @Test
    void testIsUserAuthorizedById_AllowAnonymous() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(false);
        when(formRepository.findVisibilityByFormId(formId)).thenReturn(Optional.of(Visibility.PRIVATE));
        when(formRepository.findAllowAnonymousByFormId(formId)).thenReturn(Optional.of(true));

        // Execute and verify
        assertTrue(validationService.isUserAuthorized(formId, userId));
    }

    @Test
    void testIsUserAuthorizedById_NotAuthorized() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(false);
        when(formRepository.findVisibilityByFormId(formId)).thenReturn(Optional.of(Visibility.PRIVATE));
        when(formRepository.findAllowAnonymousByFormId(formId)).thenReturn(Optional.of(false));

        // Execute and verify
        assertFalse(validationService.isUserAuthorized(formId, userId));
    }

    @Test
    void testIsUserAuthorizedToEditById_NullFormId() {
        // Test with null formId
        assertFalse(validationService.isUserAuthorizedToEdit((Long)null, userId));
    }

    @Test
    void testIsUserAuthorizedToEditById_NullUserId() {
        // Test with null userId
        assertFalse(validationService.isUserAuthorizedToEdit(formId, null));
    }

    @Test
    void testIsUserAuthorizedToEditById_UserIsOwner() {
        // Setup
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(userId));

        // Execute and verify
        assertTrue(validationService.isUserAuthorizedToEdit(formId, userId));
    }

    @Test
    void testIsUserAuthorizedToEditById_UserIsEditorCollaborator() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(true);

        // Execute and verify
        assertTrue(validationService.isUserAuthorizedToEdit(formId, userId));
    }

    @Test
    void testIsUserAuthorizedToEditById_UserIsNotAuthorized() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);

        // Execute and verify
        assertFalse(validationService.isUserAuthorizedToEdit(formId, userId));
    }

    @Test
    void testIsUserAuthorizedForCollaboratorsById_NullFormId() {
        // Test with null formId
        assertFalse(validationService.isUserAuthorizedForCollaborators((Long)null, userId));
    }

    @Test
    void testIsUserAuthorizedForCollaboratorsById_NullUserId() {
        // Test with null userId
        assertFalse(validationService.isUserAuthorizedForCollaborators(formId, null));
    }

    @Test
    void testIsUserAuthorizedForCollaboratorsById_UserIsOwner() {
        // Setup
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(userId));

        // Execute and verify
        assertTrue(validationService.isUserAuthorizedForCollaborators(formId, userId));
    }

    @Test
    void testIsUserAuthorizedForCollaboratorsById_UserIsEditorCollaborator() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(true);

        // Execute and verify
        assertTrue(validationService.isUserAuthorizedForCollaborators(formId, userId));
    }

    @Test
    void testIsUserAuthorizedForCollaboratorsById_UserIsViewerCollaborator_PrivateForm() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(true);
        when(formRepository.findVisibilityByFormId(formId)).thenReturn(Optional.of(Visibility.PRIVATE));

        // Execute and verify
        assertTrue(validationService.isUserAuthorizedForCollaborators(formId, userId));
    }

    @Test
    void testIsUserAuthorizedForCollaboratorsById_UserIsViewerCollaborator_PublicForm() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(true);
        when(formRepository.findVisibilityByFormId(formId)).thenReturn(Optional.of(Visibility.PUBLIC));

        // Execute and verify
        assertTrue(validationService.isUserAuthorizedForCollaborators(formId, userId));
    }

    @Test
    void testIsUserAuthorizedForCollaboratorsById_UserIsNotAuthorized_PrivateForm() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(false);
        when(formRepository.findVisibilityByFormId(formId)).thenReturn(Optional.of(Visibility.PRIVATE));

        // Execute and verify
        assertFalse(validationService.isUserAuthorizedForCollaborators(formId, userId));
    }

    @Test
    void testIsUserAuthorizedForCollaboratorsById_UserIsNotAuthorized_PublicForm() {
        // Setup
        Long differentUserId = 789L;
        when(formRepository.findOwnerIdByFormId(formId)).thenReturn(Optional.of(differentUserId));
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.EDITOR)).thenReturn(false);
        when(formRepository.existsByFormIdAndUserIdAndRole(formId, userId, CollaboratorRole.VIEWER)).thenReturn(false);
        when(formRepository.findVisibilityByFormId(formId)).thenReturn(Optional.of(Visibility.PUBLIC));

        // Execute and verify
        assertTrue(validationService.isUserAuthorizedForCollaborators(formId, userId));
    }
}