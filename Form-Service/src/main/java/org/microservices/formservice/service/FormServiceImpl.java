package org.microservices.formservice.service;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.FormDto;
import org.microservices.formservice.entity.Form;
import org.microservices.formservice.entity.Option;
import org.microservices.formservice.entity.Question;
import org.microservices.formservice.enums.CollaboratorRole;
import org.microservices.formservice.enums.Status;
import org.microservices.formservice.enums.Visibility;
import org.microservices.formservice.exception.ResourceNotFoundException;
import org.microservices.formservice.exception.UnauthorizedException;
import org.microservices.formservice.mappers.FormMapper;
import org.microservices.formservice.repository.CollaboratorRepository;
import org.microservices.formservice.repository.FormRepository;
import org.microservices.formservice.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private final AuthorizationService authorizationService;

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
        Form form = formRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));

        if (!authorizationService.isUserAuthorized(form, userId)) {
            throw new UnauthorizedException("You don't have permission to access this form");
        }

        return formMapper.toDto(form);
    }

    /**
     * Creates a new form entity based on the provided FormDto, saves it to the repository,
     * and returns the saved form as a FormDto.
     * The method sets the current date and time for the created and updated timestamps.
     * If no status is provided, it defaults to DRAFT. If no visibility is provided, it defaults to PRIVATE.
     *
     * @param formDto the data transfer object containing the details of the form to be created
     * @return the data transfer object representation of the saved form entity
     */
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
        Form form = formRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));
        if (!authorizationService.isUserAuthorizedToEdit(form, userId)) {
            throw new UnauthorizedException("You don't have permission to update this form");
        }
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
        Form form = formRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));
        if (!authorizationService.isFormOwner(form, userId)) {
            throw new UnauthorizedException("Only the form owner can change visibility settings");
        }
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
     * Creates a copy of an existing form with its associated questions and options.
     * The copied form will be saved as a draft with private visibility and assigned to the requesting user.
     * The method ensures that the user has the necessary permissions to copy the form.
     *
     * @param id the ID of the form to be copied
     * @param userId the ID of the user requesting to copy the form
     * @return a {@code FormDto} representing the copied form including its associated questions and options
     * @throws ResourceNotFoundException if the form with the specified ID does not exist
     * @throws UnauthorizedException if the user does not have permission to copy the form
     */
    @Override
    @Transactional
    public FormDto copyForm(Long id, Long userId) {
        Form originalForm = formRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));

        if (originalForm.getVisibility() == Visibility.PRIVATE &&
                !originalForm.getCreatedBy().equals(userId) &&
                !collabRepo.existsByFormIdAndUserId(id, userId)) {
            throw new UnauthorizedException("You don't have permission to copy this form");
        }

        Form newForm = new Form();
        newForm.setName(originalForm.getName() + " (Copy)");
        newForm.setDescription(originalForm.getDescription());
        newForm.setAllowAnonymous(originalForm.isAllowAnonymous());
        newForm.setResponseLimit(originalForm.getResponseLimit());
        newForm.setStatus(Status.DRAFT);
        newForm.setVisibility(Visibility.PRIVATE);
        newForm.setCreatedBy(userId);
        newForm.setCreatedAt(LocalDateTime.now());
        newForm.setUpdatedAt(LocalDateTime.now());
        newForm.setQuestions(new ArrayList<>());

        Form savedForm = formRepo.save(newForm);
        List<Question> originalQuestions = questionRepo.findByFormIdWithOptionsOrdered(id);
        if (originalQuestions != null && !originalQuestions.isEmpty()) {
            for (Question originalQuestion : originalQuestions) {
                Question newQuestion = new Question();
                newQuestion.setText(originalQuestion.getText());
                newQuestion.setType(originalQuestion.getType());
                newQuestion.setRequired(originalQuestion.isRequired());
                newQuestion.setOrderIndex(originalQuestion.getOrderIndex());
                newQuestion.setForm(savedForm);
                newQuestion.setOptions(new ArrayList<>());

                Question savedQuestion = questionRepo.save(newQuestion);

                if (originalQuestion.getOptions() != null && !originalQuestion.getOptions().isEmpty()) {
                    for (Option originalOption : originalQuestion.getOptions()) {
                        Option newOption = new Option();
                        newOption.setText(originalOption.getText());
                        newOption.setImageUrl(originalOption.getImageUrl());
                        newOption.setQuestion(savedQuestion);
                        savedQuestion.getOptions().add(newOption);
                    }
                    questionRepo.save(savedQuestion);
                }
            }
        }

        return formMapper.toDto(formRepo.findById(savedForm.getId()).orElse(savedForm));
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
        Form form = formRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));

        if (!authorizationService.isUserAuthorizedToEdit(form, userId)) {
            throw new UnauthorizedException("You don't have permission to lock this form");
        }

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
        Form form = formRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));

        if (!authorizationService.isUserAuthorizedToEdit(form, userId)) {
            throw new UnauthorizedException("You don't have permission to unlock this form");
        }

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
        Form form = formRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));

        if (!authorizationService.isUserAuthorizedToEdit(form, userId)) {
            throw new UnauthorizedException("You don't have permission to update this form");
        }

        form.setName(formDto.getName());
        form.setDescription(formDto.getDescription());
        form.setAllowAnonymous(formDto.isAllowAnonymous());

        if (formDto.getStatus() != null) {
            form.setStatus(formDto.getStatus());
        }

        if (formDto.getVisibility() != null) {
            if (!authorizationService.isFormOwner(form, userId)) {
                throw new UnauthorizedException("Only the form owner can change visibility settings");
            }
            form.setVisibility(formDto.getVisibility());
        }

        form.setUpdatedAt(LocalDateTime.now());

        return formMapper.toDto(formRepo.save(form));
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
    public void deleteForm(Long id, Long userId) {
        Form form = formRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with ID: " + id));

        if (!authorizationService.isFormOwner(form, userId)) {
            throw new UnauthorizedException("Only the form owner can delete this form");
        }

        collabRepo.deleteByFormId(id);
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