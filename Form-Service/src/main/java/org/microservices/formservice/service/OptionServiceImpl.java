package org.microservices.formservice.service;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.OptionDto;
import org.microservices.formservice.entity.Form;
import org.microservices.formservice.entity.Option;
import org.microservices.formservice.entity.Question;
import org.microservices.formservice.exception.ResourceNotFoundException;
import org.microservices.formservice.exception.UnauthorizedException;
import org.microservices.formservice.mappers.OptionMapper;
import org.microservices.formservice.repository.CollaboratorRepository;
import org.microservices.formservice.repository.OptionRepository;
import org.microservices.formservice.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Implementation of the OptionService interface for managing options in the context of a form or survey system.
 * Provides methods for creating, retrieving, updating, and deleting options, ensuring proper authorization and validation.
 */
@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final OptionMapper optionMapper;
    private final AuthorizationService authorizationService;

    /**
     * Retrieves a list of options associated with a specific question.
     *
     * @param questionId the unique identifier of the question whose options are being retrieved
     * @param userId the unique identifier of the user making the request
     * @return a list of OptionDto objects representing the options associated with the specified question
     * @throws ResourceNotFoundException if the question with the given ID does not exist
     * @throws UnauthorizedException if the user is not authorized to view options for the specified question
     */
    @Override
    @Transactional(readOnly = true)
    public List<OptionDto> getOptionsByQuestion(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        if (!isUserAuthorized(question.getForm(), userId)) {
            throw new UnauthorizedException("User is not authorized to view options for this question");
        }

        return optionRepository.findByQuestionId(questionId)
                .stream()
                .map(optionMapper::toDto)
                .toList();
    }

    /**
     * Creates a new option associated with a specific question.
     * Ensures that the user has appropriate authorization to add options to the question.
     * Converts the OptionDto into an Option entity, associates it with the given question,
     * and persists it to the database.
     *
     * @param questionId the unique identifier of the question to which the option will be added
     * @param dto the data transfer object containing the details of the option to be created
     * @param userId the unique identifier of the user attempting to create the option
     * @return the created option as a data transfer object
     * @throws ResourceNotFoundException if the question with the specified ID does not exist
     * @throws UnauthorizedException if the user is not authorized to add options to the question
     */
    @Override
    @Transactional
    public OptionDto createOption(Long questionId, OptionDto dto, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        if (!authorizationService.isUserAuthorizedToEdit(question.getForm(), userId)) {
            throw new UnauthorizedException("User is not authorized to add options to this question");
        }

        Option option = optionMapper.toEntity(dto);
        option.setQuestion(question);

        return optionMapper.toDto(optionRepository.save(option));
    }

    /**
     * Updates the details of an existing option.
     *
     * @param id the ID of the option to be updated
     * @param dto the data transfer object containing the new option details
     * @param userId the ID of the user attempting the update, for authorization purposes
     * @return the updated option as a data transfer object
     * @throws ResourceNotFoundException if the option or related resources (e.g., question) are not found
     * @throws UnauthorizedException if the user is not authorized to update the option
     */
    @Override
    @Transactional
    public OptionDto updateOption(Long id, OptionDto dto, Long userId) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + id));

        if (!authorizationService.isUserAuthorizedToEdit(option.getQuestion().getForm(), userId)) {
            throw new UnauthorizedException("User is not authorized to update this option");
        }

        if (dto.getQuestionId() != null) {
            Question question = questionRepository.findById(dto.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + dto.getQuestionId()));

            if (!authorizationService.isUserAuthorizedToEdit(question.getForm(), userId)) {
                throw new UnauthorizedException("User is not authorized to add options to this question");
            }

            option.setQuestion(question);
        }

        option.setText(dto.getText());
        option.setImageUrl(dto.getImageUrl());

        return optionMapper.toDto(optionRepository.save(option));
    }

    /**
     * Deletes an option by its ID after checking user authorization.
     *
     * @param id the ID of the option to be deleted
     * @param userId the ID of the user attempting to delete the option
     * @throws ResourceNotFoundException if the option with the given ID is not found
     * @throws UnauthorizedException if the user is not authorized to delete the option
     */
    @Override
    @Transactional
    public void deleteOption(Long id, Long userId) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + id));

        if (!authorizationService.isUserAuthorizedToEdit(option.getQuestion().getForm(), userId)) {
            throw new UnauthorizedException("User is not authorized to delete this option");
        }

        optionRepository.deleteById(id);
    }

    private boolean isUserAuthorized(Form form, Long userId) {
        return authorizationService.isUserAuthorized(form, userId);
    }
}