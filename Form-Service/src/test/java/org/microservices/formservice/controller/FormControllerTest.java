package org.microservices.formservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.microservices.formservice.DTO.FormDto;
import org.microservices.formservice.DTO.QuestionDto;
import org.microservices.formservice.enums.QuestionType;
import org.microservices.formservice.enums.Status;
import org.microservices.formservice.enums.Visibility;
import org.microservices.formservice.service.FormService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FormControllerTest {

    @Mock
    private FormService formService;

    @InjectMocks
    private FormController formController;

    private FormDto formDto;
    private List<FormDto> formDtoList;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 123L;

        // Create a test question
        QuestionDto questionDto = QuestionDto.builder()
                .text("Test Question")
                .type(QuestionType.SHORT_TEXT)
                .required(true)
                .build();

        List<QuestionDto> questions = new ArrayList<>();
        questions.add(questionDto);

        formDto = FormDto.builder()
                .id(1L)
                .name("Test Form")
                .description("Test Description")
                .createdBy(userId)
                .questions(questions)
                .build();

        formDtoList = new ArrayList<>();
        formDtoList.add(formDto);
    }

    @Test
    void testGetAllForms() {
        // Setup
        when(formService.getAllForms()).thenReturn(formDtoList);

        // Execute
        ResponseEntity<List<FormDto>> response = formController.getAllForms();

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formDtoList, response.getBody());
        verify(formService).getAllForms();
    }

    @Test
    void testGetFormById() {
        // Setup
        when(formService.getFormById(anyLong(), anyLong())).thenReturn(formDto);

        // Execute
        ResponseEntity<FormDto> response = formController.getFormById(1L, userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formDto, response.getBody());
        verify(formService).getFormById(1L, userId);
    }

    @Test
    void testCreateForm() {
        // Setup
        when(formService.createForm(any(FormDto.class))).thenReturn(formDto);

        // Execute
        ResponseEntity<FormDto> response = formController.createForm(formDto, userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formDto, response.getBody());
        assertEquals(userId, formDto.getCreatedBy());
        verify(formService).createForm(formDto);
    }

    @Test
    void testGetUserForms() {
        // Setup
        when(formService.getUserForms(anyLong())).thenReturn(formDtoList);

        // Execute
        ResponseEntity<List<FormDto>> response = formController.getUserForms(userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formDtoList, response.getBody());
        verify(formService).getUserForms(userId);
    }

    @Test
    void testUpdateFormStatus() {
        // Setup
        Status status = Status.ACTIVE;
        when(formService.updateFormStatus(anyLong(), any(Status.class), anyLong())).thenReturn(formDto);

        // Execute
        ResponseEntity<FormDto> response = formController.updateFormStatus(1L, status, userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formDto, response.getBody());
        verify(formService).updateFormStatus(1L, status, userId);
    }

    @Test
    void testUpdateFormVisibility() {
        // Setup
        Visibility visibility = Visibility.PUBLIC;
        when(formService.updateFormVisibility(anyLong(), any(Visibility.class), anyLong())).thenReturn(formDto);

        // Execute
        ResponseEntity<FormDto> response = formController.updateFormVisibility(1L, visibility, userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formDto, response.getBody());
        verify(formService).updateFormVisibility(1L, visibility, userId);
    }

    @Test
    void testGetFormsByStatus() {
        // Setup
        Status status = Status.ACTIVE;
        when(formService.getFormsByStatus(any(Status.class))).thenReturn(formDtoList);

        // Execute
        ResponseEntity<List<FormDto>> response = formController.getFormsByStatus(status);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formDtoList, response.getBody());
        verify(formService).getFormsByStatus(status);
    }

    @Test
    void testGetFormsByVisibility() {
        // Setup
        Visibility visibility = Visibility.PUBLIC;
        when(formService.getFormsByVisibility(any(Visibility.class))).thenReturn(formDtoList);

        // Execute
        ResponseEntity<List<FormDto>> response = formController.getFormsByVisibility(visibility);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formDtoList, response.getBody());
        verify(formService).getFormsByVisibility(visibility);
    }

    @Test
    void testGetPublicForms() {
        // Setup
        when(formService.getPublicForms()).thenReturn(formDtoList);

        // Execute
        ResponseEntity<List<FormDto>> response = formController.getPublicForms();

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formDtoList, response.getBody());
        verify(formService).getPublicForms();
    }

    @Test
    void testCopyForm() {
        // Setup
        when(formService.copyForm(anyLong(), anyLong())).thenReturn(formDto);

        // Execute
        ResponseEntity<FormDto> response = formController.copyForm(1L, userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formDto, response.getBody());
        verify(formService).copyForm(1L, userId);
    }

    @Test
    void testLockForm() {
        // Setup
        when(formService.lockForm(anyLong(), anyLong())).thenReturn(formDto);

        // Execute
        ResponseEntity<FormDto> response = formController.lockForm(1L, userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formDto, response.getBody());
        verify(formService).lockForm(1L, userId);
    }

    @Test
    void testUnlockForm() {
        // Setup
        when(formService.unlockForm(anyLong(), anyLong())).thenReturn(formDto);

        // Execute
        ResponseEntity<FormDto> response = formController.unlockForm(1L, userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formDto, response.getBody());
        verify(formService).unlockForm(1L, userId);
    }

    @Test
    void testUpdateForm() {
        // Setup
        when(formService.updateForm(anyLong(), any(FormDto.class), anyLong())).thenReturn(formDto);

        // Execute
        ResponseEntity<FormDto> response = formController.updateForm(1L, formDto, userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formDto, response.getBody());
        verify(formService).updateForm(1L, formDto, userId);
    }

    @Test
    void testDeleteForm() {
        // Execute
        ResponseEntity<Void> response = formController.deleteForm(1L, userId);

        // Verify
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(formService).deleteForm(1L, userId);
    }
}