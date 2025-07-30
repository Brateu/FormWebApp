package org.microservices.formservice.service;

import org.microservices.formservice.DTO.OptionDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service interface for managing options in the context of a form or survey system.
 * Provides methods for creating, retrieving, updating, and deleting options associated with a question.
 */
@Service
public interface OptionService {
    /**
     * Retrieves a list of options associated with a specific question for a user.
     *
     * @param questionId the unique identifier of the question whose options are to be retrieved
     * @param userId the unique identifier of the user requesting the options
     * @return a list of OptionDto objects representing the options associated with the specified question
     */
    List<OptionDto> getOptionsByQuestion(Long questionId, Long userId);
    /**
     * Creates a new option associated with a specific question.
     *
     * @param questionId the unique identifier of the question to which the option will be linked
     * @param dto the OptionDto object containing the details of the option to be created
     * @param userId the unique identifier of the user performing the operation
     * @return the newly created OptionDto object
     */
    OptionDto createOption(Long questionId, OptionDto dto, Long userId);
    /**
     * Updates an existing option with new details provided in the given OptionDto.
     *
     * @param id the unique identifier of the option to be updated
     * @param dto the OptionDto object containing the updated details of the option
     * @param userId the unique identifier of the user performing the update operation
     * @return the updated OptionDto object after successful modification
     */
    OptionDto updateOption(Long id, OptionDto dto, Long userId);
    /**
     * Deletes an existing option associated with a specific identifier.
     *
     * @param id the unique identifier of the option to be deleted
     * @param userId the unique identifier of the user performing the deletion
     */
    void deleteOption(Long id, Long userId);
}