package org.microservices.formservice.service;

import org.microservices.formservice.DTO.FormDto;
import org.microservices.formservice.enums.Visibility;
import org.microservices.formservice.enums.Status;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service interface for managing forms in the application.
 * Provides methods for various form-related operations such as creation, retrieval, updating,
 * and deletion of forms.
 */
@Service
public interface FormService {
    /**
     * Retrieves a list of all forms available in the system.
     *
     * @return a list of FormDto objects representing the forms.
     */
    List<FormDto> getAllForms();
    /**
     * Retrieves a specific form by its identifier and the associated user identifier.
     *
     * @param id the unique identifier of the form to be retrieved
     * @param userId the unique identifier of the user requesting the form
     * @return a FormDto object containing the details of the requested form,
     *         or null if the form is not found or not accessible to the specified user
     */
    FormDto getFormById(Long id, Long userId);
    /**
     * Creates a new form based on the provided FormDto object.
     *
     * @param formDto the FormDto object containing the details of the form to be created
     * @return the created FormDto object with the updated details, such as the generated ID and timestamps
     */
    FormDto createForm(FormDto formDto);
    /**
     * Retrieves a list of forms associated with a specific user.
     *
     * @param userId the unique identifier of the user whose forms are to be retrieved
     * @return a list of FormDto objects representing the forms associated with the user
     */
    List<FormDto> getUserForms(Long userId);
    /**
     * Updates the status of a form identified by its unique identifier.
     *
     * @param id the unique identifier of the form whose status is to be updated
     * @param status the new status to be assigned to the form
     * @param userId the unique identifier of the user performing the update
     * @return the updated FormDto object containing the details of the form with the new status
     */
    FormDto updateFormStatus(Long id, Status status, Long userId);
    /**
     * Updates the visibility of a form identified by its unique identifier.
     *
     * @param id the unique identifier of the form whose visibility is to be updated
     * @param visibility the new visibility setting to be assigned to the form
     * @param userId the unique identifier of the user performing the visibility update
     * @return the updated FormDto object containing the details of the form with the new visibility
     */
    FormDto updateFormVisibility(Long id, Visibility visibility, Long userId);
    /**
     * Retrieves a list of forms based on their status.
     *
     * @param status the status of the forms to be retrieved
     * @return a list of FormDto objects that have the specified status
     */
    List<FormDto> getFormsByStatus(Status status);
    /**
     * Retrieves a list of forms filtered by their visibility setting.
     *
     * @param visibility the visibility level used to filter the forms
     * @return a list of FormDto objects representing the forms matching the specified visibility
     */
    List<FormDto> getFormsByVisibility(Visibility visibility);
    /**
     * Retrieves a list of all publicly available forms.
     *
     * @return a list of FormDto objects representing the public forms.
     */
    List<FormDto> getPublicForms();
    /**
     * Retrieves a publicly accessible form by its unique identifier.
     *
     * @param id the unique identifier of the public form to be retrieved
     * @return a FormDto object representing the details of the public form,
     *         or null if the form is not found or is not publicly accessible
     */
    FormDto getPublicFormById(Long id);
    /**
     * Locks a form identified by its unique identifier for a specific user.
     * This operation restricts access or modifications to the form, locking it for further changes.
     *
     * @param id the unique identifier of the form to be locked
     * @param userId the unique identifier of the user performing the lock operation
     * @return the locked FormDto object containing the details of the form
     */
    FormDto lockForm(Long id, Long userId);
    /**
     * Unlocks a form identified by its unique identifier for a specified user.
     *
     * @param id the unique identifier of the form to be unlocked
     * @param userId the unique identifier of the user performing the unlock operation
     * @return a FormDto object representing the details of the unlocked form
     */
    FormDto unlockForm(Long id, Long userId);
    /**
     * Updates the form with the specified ID using the provided form details and user ID.
     *
     * @param id the ID of the form to be updated
     * @param formDto the DTO containing updated form details
     * @param userId the ID of the user performing the update
     * @return the updated form data as a FormDto object
     */
    FormDto updateForm(Long id, FormDto formDto, Long userId);
    /**
     * Deletes a form identified by its unique ID and associated user ID.
     *
     * @param id the unique identifier of the form to be deleted
     * @param userId the unique identifier of the user associated with the form
     */
    void deleteForm(Long id, Long userId);
}