package org.microservices.formservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.FormDto;
import org.microservices.formservice.DTO.OptionDto;
import org.microservices.formservice.DTO.QuestionDto;
import org.microservices.formservice.entity.Form;
import org.microservices.formservice.entity.Option;
import org.microservices.formservice.entity.Question;
import org.microservices.formservice.enums.Status;
import org.microservices.formservice.enums.Visibility;
import org.microservices.formservice.exception.ResourceNotFoundException;
import org.microservices.formservice.exception.UnauthorizedException;
import org.microservices.formservice.mappers.FormMapper;
import org.microservices.formservice.mappers.FormToDto;
import org.microservices.formservice.repository.CollaboratorRepository;
import org.microservices.formservice.repository.FormRepository;
import org.microservices.formservice.repository.QuestionRepository;
import org.microservices.formservice.service.FormService;
import org.microservices.formservice.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * The FormServiceImpl class is an implementation of the FormService interface.
 * It provides functionality for managing forms, including CRUD operations,
 * handling authorizations, and enforcing business rules related to form access, visibility,
 * status updates, and collaborations.
 *
 * This service interacts with repositories and other service classes to manage
 * persistence and mapping operations related to forms and their associated entities.
 */
@Service
@RequiredArgsConstructor
public class FormServiceImpl implements FormService {

    private final FormRepository formRepo;
    private final CollaboratorRepository collabRepo;
    private final QuestionRepository questionRepo;
    private final FormMapper formMapper;
    private final ValidationService validationService;
    private final QuestionService questionService;
    private final FormToDto formToDto;

    /**
     * Retrieves a list of all forms and maps them to DTO objects.
     *
     * @return a list of FormDto representing all forms in the repository.
     */
    @Override
    public List<FormDto> getAllForms() {
        return formRepo.findAll()
                .stream()
                .map(formMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a form by its ID and ensures the user is authorized to access it.
     *
     * @param id the unique identifier of the form to be retrieved
     * @param userId the unique identifier of the user requesting the form
     * @return the form data as a FormDto object if the form exists and the user is authorized to access it
     * @throws ResourceNotFoundException if no form is found with the given ID
     * @throws UnauthorizedException if the user is not authorized to access the form
     */
    @Override
    @Transactional(readOnly = true)
    public FormDto getFormById(Long id, Long userId) {
        if (!validationService.isUserAuthorized(id, userId) && userId != null) {
            throw new UnauthorizedException("You don't have permission to access this form");
        }

        Form form = formRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));
        return formToDto.formDto(form);
    }

    /**
     * Creates a new form entity based on the provided FormDto, saves it to the repository,
     * and returns the saved form as a FormDto.
     * The method sets the current date and time for the created and updated timestamps.
     * If no status is provided, it defaults to DRAFT. If no visibility is provided, it defaults to PRIVATE.
     * At least one question must be provided when creating a form.
     *
     * @param formDto the data transfer object containing the details of the form to be created
     * @return the data transfer object representation of the saved form entity
     * @throws IllegalArgumentException if no questions are provided
     */
    @Override
    @Transactional
    public FormDto createForm(FormDto formDto) {
        // Validate that at least one question is provided
        if (formDto.getQuestions() == null || formDto.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("At least one question must be provided when creating a form");
        }

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

        // Create questions for the form
        List<QuestionDto> createdQuestions = new ArrayList<>();
        for (QuestionDto questionDto : formDto.getQuestions()) {
            QuestionDto createdQuestion = questionService.createQuestion(form.getId(), questionDto, formDto.getCreatedBy());
            createdQuestions.add(createdQuestion);
        }

        // Get the updated form with questions
        FormDto createdFormDto = formMapper.toDto(form);
        createdFormDto.setQuestions(createdQuestions);

        return createdFormDto;
    }

    /**
     * Retrieves a list of forms associated with a specific user. These forms include
     * the ones created by the user and those where the user is a collaborator.
     *
     * @param userId the ID of the user for whom the forms are retrieved
     * @return a list of FormDto objects representing the forms associated with the user
     */
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

    /**
     * Updates the status of the form with the given ID.
     *
     * @param id the ID of the form to update
     * @param status the new status to be set for the form
     * @param userId the ID of the user performing the update
     * @return a DTO representation of the updated form
     * @throws ResourceNotFoundException if no form is found with the given ID
     * @throws UnauthorizedException if the user is not authorized to update the form
     */
    @Override
    public FormDto updateFormStatus(Long id, Status status, Long userId) {
        if (!validationService.isUserAuthorizedToEdit(id, userId)) {
            throw new UnauthorizedException("You don't have permission to update this form");
        }

        Form form = formRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));
        form.setStatus(status);
        form.setUpdatedAt(LocalDateTime.now());
        form = formRepo.save(form);

        return formMapper.toDto(form);
    }

    /**
     * Updates the visibility of a form and persists the changes.
     *
     * @param id the unique identifier of the form to be updated
     * @param visibility the new visibility setting to be applied to the form
     * @param userId the unique identifier of the user attempting to update the form visibility
     * @return a Data Transfer Object (DTO) representing the updated form
     * @throws ResourceNotFoundException if the form with the specified ID does not exist
     * @throws UnauthorizedException if the user is not authorized to update the form's visibility
     */
    @Override
    public FormDto updateFormVisibility(Long id, Visibility visibility, Long userId) {
        if (!validationService.isFormOwner(id, userId) && !validationService.isCollaboratorEditor(id, userId)) {
            throw new UnauthorizedException("Only the form owner or editor can change visibility settings");
        }

        Form form = formRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));
        form.setVisibility(visibility);
        form.setUpdatedAt(LocalDateTime.now());
        form = formRepo.save(form);

        return formMapper.toDto(form);
    }

    /**
     * Retrieves a list of forms based on the provided status.
     *
     * @param status the status used to filter the forms
     * @return a list of FormDto objects corresponding to the filtered forms
     */
    @Override
    public List<FormDto> getFormsByStatus(Status status) {
        return formRepo.findByStatus(status)
                .stream()
                .map(formMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a list of forms filtered by their visibility.
     *
     * @param visibility the visibility criteria to filter the forms
     * @return a list of forms as FormDto objects that match the specified visibility
     */
    @Override
    public List<FormDto> getFormsByVisibility(Visibility visibility) {
        return formRepo.findByVisibility(visibility)
                .stream()
                .map(formMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a list of public forms that allow anonymous access.
     *
     * @return a list of FormDto objects representing public forms with anonymous access allowed
     */
    @Override
    public List<FormDto> getPublicForms() {
        List<Form> publicForms = formRepo.findByVisibility(Visibility.PUBLIC);

        List<Form> publicAndAnonymousForms = publicForms.stream()
                .filter(Form::isAllowAnonymous)
                .toList();

        return publicAndAnonymousForms.stream()
                .map(formMapper::toDto)
                .toList();
    }
    /**
     * Locks a form for editing by setting its locked status to true. Ensures the user has
     * the appropriate authorization to lock the form before proceeding. Updates the
     * modification timestamp of the form upon successful lock.
     *
     * @param id the ID of the form to be locked
     * @param userId the ID of the user attempting to lock the form
     * @return a DTO representation of the locked form
     * @throws ResourceNotFoundException if the form with the specified ID is not found
     * @throws UnauthorizedException if the user is not authorized to lock the form
     */
    @Override
    public FormDto lockForm(Long id, Long userId) {
        if (!validationService.isUserAuthorizedToEdit(id, userId)) {
            throw new UnauthorizedException("You don't have permission to lock this form");
        }

        Form form = formRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));
        form.setLocked(true);
        form.setUpdatedAt(LocalDateTime.now());
        form = formRepo.save(form);

        return formMapper.toDto(form);
    }

    /**
     * Unlocks a form by its ID, allowing further edits, and ensures the user has the authorization to perform this operation.
     *
     * @param id the ID of the form to be unlocked
     * @param userId the ID of the user attempting to unlock the form
     * @return the unlocked form as a FormDto object
     * @throws ResourceNotFoundException if the form with the given ID is not found
     * @throws UnauthorizedException if the user does not have permission to unlock the form
     */
    @Override
    public FormDto unlockForm(Long id, Long userId) {
        if (!validationService.isUserAuthorizedToEdit(id, userId)) {
            throw new UnauthorizedException("You don't have permission to unlock this form");
        }

        Form form = formRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));
        form.setLocked(false);
        form.setUpdatedAt(LocalDateTime.now());
        form = formRepo.save(form);

        return formMapper.toDto(form);
    }

    /**
     * Updates an existing form with new details provided in the {@code formDto}.
     * Performs authorization checks to ensure the requesting user has the necessary permissions
     * to update the form and modify specific fields such as visibility.
     *
     * @param id the ID of the form to be updated
     * @param formDto the data transfer object containing updated form details
     * @param userId the ID of the user requesting the update
     * @return the updated form as a data transfer object
     * @throws ResourceNotFoundException if the form with the specified ID does not exist
     * @throws UnauthorizedException if the user is not authorized to update the form or modify its visibility
     */
    @Override
    public FormDto updateForm(Long id, FormDto formDto, Long userId) {
        if (!validationService.isUserAuthorizedToEdit(id, userId)) {
            throw new UnauthorizedException("You don't have permission to update this form");
        }

        Form form = formRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));

        form.setName(formDto.getName());
        form.setDescription(formDto.getDescription());
        form.setAllowAnonymous(formDto.isAllowAnonymous());
        form.setLocked(formDto.isLocked());
        if (formDto.getStatus() != null) {
            form.setStatus(formDto.getStatus());
        }
        if (formDto.getVisibility() != null) {
            form.setVisibility(formDto.getVisibility());
        }
        form.setUpdatedAt(LocalDateTime.now());

        Map<Long, Question> existingQuestionsById = new HashMap<>();
        for (Question q : form.getQuestions()) {
            if (q.getId() != null) {
                existingQuestionsById.put(q.getId(), q);
            }
        }

        Set<Long> incomingQuestionIds = new HashSet<>();

        if (formDto.getQuestions() != null) {
            for (QuestionDto qd : formDto.getQuestions()) {
                if (qd.getId() != null) {
                    incomingQuestionIds.add(qd.getId());
                }
            }
        }

        form.getQuestions().removeIf(q -> q.getId() != null && !incomingQuestionIds.contains(q.getId()));

        int idx = 0;
        if (formDto.getQuestions() != null) {
            for (QuestionDto qd : formDto.getQuestions()) {
                Question q = (qd.getId() != null) ? existingQuestionsById.get(qd.getId()) : null;

                if (q == null) {
                    q = new Question();
                    q.setForm(form);
                    q.setUserId(userId);
                    form.getQuestions().add(q);
                }

                q.setText(qd.getText());
                q.setType(qd.getType());
                q.setRequired(qd.isRequired());
                q.setOrderIndex(qd.getOrderIndex() != null ? qd.getOrderIndex() : idx++);
                q.setImageUrl(qd.getImageUrl());

                Map<Long, Option> existingOptionsById = new HashMap<>();
                for (Option o : q.getOptions()) {
                    if (o.getId() != null) {
                        existingOptionsById.put(o.getId(), o);
                    }
                }

                Set<Long> incomingOptionIds = new HashSet<>();
                if (qd.getOptions() != null) {
                    for (OptionDto od : qd.getOptions()) {
                        if (od.getId() != null) {
                            incomingOptionIds.add(od.getId());
                        }
                    }
                }

                q.getOptions().removeIf(o -> o.getId() != null && !incomingOptionIds.contains(o.getId()));

                if (qd.getOptions() != null) {
                    for (OptionDto od : qd.getOptions()) {
                        Option o = (od.getId() != null) ? existingOptionsById.get(od.getId()) : null;
                        if (o == null) {
                            o = new Option();
                            o.setQuestion(q);
                            q.getOptions().add(o);
                        }
                        o.setText(od.getText());
                        o.setImageUrl(od.getImageUrl());

                    }
                }

                form.getQuestions().add(q);
            }
        }

        Form saved = formRepo.save(form);
        return formMapper.toDto(saved);
    }

    /**
     * Deletes a form by its ID if the user is authorized as the owner of the form.
     * The method first retrieves the form and checks if the specified user is the owner.
     * If the user is not the owner, an UnauthorizedException is thrown.
     * Once authorized, the associated collaborators and the form itself are deleted.
     *
     * @param id the ID of the form to be deleted
     * @param userId the ID of the user attempting to delete the form
     * @throws ResourceNotFoundException if the form with the specified ID does not exist
     * @throws UnauthorizedException if the user is not authorized to delete the form
     */
    @Override
    @Transactional
    public void deleteForm(Long id, Long userId) {
        if (!validationService.isFormOwner(id, userId)) {
            throw new UnauthorizedException("Only the form owner can delete this form");
        }

        Form form = formRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));

        formRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public FormDto getPublicFormById(Long id) {
        Form form = formRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));

        if (form.getVisibility() == Visibility.PUBLIC && form.isAllowAnonymous()) {
            return formMapper.toDto(form);
        }

        throw new UnauthorizedException("This form is not available for public access");
    }
}