package org.microservices.formservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.QuestionDto;
import org.microservices.formservice.entity.Form;
import org.microservices.formservice.entity.Option;
import org.microservices.formservice.entity.Question;
import org.microservices.formservice.exception.ResourceNotFoundException;
import org.microservices.formservice.exception.UnauthorizedException;
import org.microservices.formservice.mappers.QuestionMapper;
import org.microservices.formservice.repository.CollaboratorRepository;
import org.microservices.formservice.repository.FormRepository;
import org.microservices.formservice.repository.QuestionRepository;
import org.microservices.formservice.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Implementation of the QuestionService interface for managing questions related to forms.
 * This service provides methods for CRUD operations on questions, managing their order,
 * and ensuring proper authorization for each operation.
 */
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final FormRepository formRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final QuestionMapper questionMapper;
    private final ValidationService validationService;

    /**
     * Retrieves the list of questions associated with a specific form, based on the given form ID and user ID.
     *
     * @param formId the unique identifier of the form whose questions are to be retrieved
     * @param userId the unique identifier of the user requesting to view the questions
     * @return a list of QuestionDto objects representing the questions of the given form
     * @throws ResourceNotFoundException if no form is found with the provided form ID
     * @throws UnauthorizedException if the user is not authorized to view the questions of the specified form
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuestionDto> getQuestionsByForm(Long formId, Long userId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + formId));

        if (!isUserAuthorized(form, userId)) {
            throw new UnauthorizedException("User is not authorized to view this form's questions");
        }

        List<Question> questions = questionRepository.findByFormIdWithOptionsOrdered(formId);
        return questionMapper.toDtoList(questions);
    }

    /**
     * Retrieves a question by its unique identifier and ensures the user is authorized to view it.
     *
     * @param id the unique identifier of the question to retrieve
     * @param userId the unique identifier of the user attempting to retrieve the question
     * @return a QuestionDto object representing the retrieved question
     * @throws ResourceNotFoundException if no question is found with the given id
     * @throws UnauthorizedException if the user is not authorized to view the question
     */
    @Override
    @Transactional(readOnly = true)
    public QuestionDto getQuestionById(Long id, Long userId) {
        Question question = questionRepository.findByIdWithOptions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        if (!isUserAuthorized(question.getForm(), userId)) {
            throw new UnauthorizedException("User is not authorized to view this question");
        }

        return questionMapper.toDto(question);
    }

    /**
     * Creates a new question and associates it with the specified form.
     * This method ensures that the user is authorized to edit the form
     * and assigns an order index to the question if not already specified.
     *
     * @param formId the ID of the form to which the question will be added
     * @param questionDto the data transfer object containing the details of the question to be created
     * @param userId the ID of the user attempting to create the question
     * @return the data transfer object representing the newly created question
     * @throws ResourceNotFoundException if the form with the specified ID does not exist
     * @throws UnauthorizedException if the user is not authorized to add questions to the form
     */
    @Override
    @Transactional
    public QuestionDto createQuestion(Long formId, QuestionDto questionDto, Long userId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + formId));

        if (!validationService.isUserAuthorizedToEdit(form, userId)) {
            throw new UnauthorizedException("User is not authorized to add questions to this form");
        }

        if (questionDto.getOrderIndex() == null) {
            Integer maxOrderIndex = questionRepository.findMaxOrderIndexByFormId(formId).orElse(-1);
            questionDto.setOrderIndex(maxOrderIndex + 1);
        }
        questionDto.setFormId(formId);
        questionDto.setUserId(userId);

        Question question = questionMapper.toEntity(questionDto);
        question.setForm(form);
        question.setUserId(userId);

        // Ensure the form is properly set before saving
        if (question.getForm() == null) {
            question.setForm(form);
        }

        // Handle options - ensure bidirectional relationship is properly set
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            List<Option> options = new ArrayList<>(question.getOptions());
            question.getOptions().clear();
            for (Option option : options) {
                question.addOption(option);
            }
        }

        Question savedQuestion = questionRepository.save(question);
        return questionMapper.toDto(savedQuestion);
    }

    /**
     * Updates an existing question with the provided data.
     *
     * @param id the ID of the question to update
     * @param questionDto the data to update the question with
     * @param userId the ID of the user performing the update
     * @return a {@code QuestionDto} representing the updated question
     * @throws ResourceNotFoundException if the question with the given ID does not exist
     * @throws UnauthorizedException if the user is not authorized to update or move the question
     */
    @Override
    @Transactional
    public QuestionDto updateQuestion(Long id, QuestionDto questionDto, Long userId) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        if (!validationService.isUserAuthorizedToEdit(question.getForm(), userId)) {
            throw new UnauthorizedException("User is not authorized to update this question");
        }

        if (questionDto.getFormId() != null && !questionDto.getFormId().equals(question.getForm().getId())) {
            Form newForm = formRepository.findById(questionDto.getFormId())
                    .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + questionDto.getFormId()));

            if (!validationService.isUserAuthorizedToEdit(newForm, userId)) {
                throw new UnauthorizedException("User is not authorized to move question to the specified form");
            }

            question.setForm(newForm);
        }

        questionDto.setUserId(userId);
        question.setUserId(userId);

        if (questionDto.getOrderIndex() == null) {
            questionDto.setOrderIndex(question.getOrderIndex());
        }

        if (questionDto.getType() == null) {
            questionDto.setType(question.getType());
        }

        if (questionDto.getText() == null) {
            questionDto.setText(question.getText());
        }

        questionMapper.updateEntityFromDto(questionDto, question);

        // Handle options - ensure bidirectional relationship is properly set
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            List<Option> options = new ArrayList<>(question.getOptions());
            question.getOptions().clear();
            for (Option option : options) {
                question.addOption(option);
            }
        }

        Question updatedQuestion = questionRepository.save(question);
        return questionMapper.toDto(updatedQuestion);
    }

    /**
     * Deletes a question specified by its ID, ensuring the user is authorized to perform the operation.
     *
     * @param id the ID of the question to be deleted
     * @param userId the ID of the user attempting to delete the question
     * @throws ResourceNotFoundException if the question with the specified ID does not exist
     * @throws UnauthorizedException if the user is not authorized to delete the question
     */
    @Override
    @Transactional
    public void deleteQuestion(Long id, Long userId) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        if (!validationService.isUserAuthorizedToEdit(question.getForm(), userId)) {
            throw new UnauthorizedException("User is not authorized to delete this question");
        }

        questionRepository.delete(question);
    }

    /**
     * Clones an existing question, creating a new one with the same properties
     * but with an updated order index and associated user ID. The method also
     * clones the associated options if present.
     *
     * @param id the ID of the existing question to clone
     * @param userId the ID of the user performing the clone operation
     * @return a {@code QuestionDto} representing the newly cloned question
     * @throws ResourceNotFoundException if the question with the given ID does not exist
     * @throws UnauthorizedException if the user is not authorized to clone the question
     */
    @Override
    @Transactional
    public QuestionDto cloneQuestion(Long id, Long userId) {
        Question originalQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        if (!validationService.isUserAuthorizedToEdit(originalQuestion.getForm(), userId)) {
            throw new UnauthorizedException("User is not authorized to clone this question");
        }

        Question clonedQuestion = new Question();
        clonedQuestion.setForm(originalQuestion.getForm());
        clonedQuestion.setText(originalQuestion.getText());
        clonedQuestion.setType(originalQuestion.getType());
        clonedQuestion.setRequired(originalQuestion.isRequired());
        clonedQuestion.setUserId(userId);

        Integer maxOrderNum = questionRepository.findMaxOrderNumByFormId(originalQuestion.getForm().getId());
        clonedQuestion.setOrderIndex(maxOrderNum != null ? maxOrderNum + 1 : 1);

        Question savedQuestion = questionRepository.save(clonedQuestion);
        if (originalQuestion.getOptions() != null && !originalQuestion.getOptions().isEmpty()) {
            for (Option originalOption : originalQuestion.getOptions()) {
                Option newOption = new Option();
                newOption.setText(originalOption.getText());
                newOption.setImageUrl(originalOption.getImageUrl());
                savedQuestion.addOption(newOption);
            }
            savedQuestion = questionRepository.save(savedQuestion);
        }

        return questionMapper.toDto(savedQuestion);
    }

    /**
     * Reorders the questions in a form based on the provided list of question IDs.
     *
     * Updates the order index of the questions to match their order in the given list,
     * ensuring that all the questions belong to the specified form and the user is authorized to make changes.
     *
     * @param formId the ID of the form containing the questions to be reordered
     * @param questionIds the list of question IDs in the desired order
     * @param userId the ID of the user performing the reorder operation
     * @return a list of updated questions in the form of DTOs reflecting the new order
     * @throws ResourceNotFoundException if the form or any of the specified questions do not exist
     * @throws UnauthorizedException if the user is not authorized to modify the form's questions
     * @throws IllegalArgumentException if any of the specified questions do not belong to the given form
     */
    @Override
    @Transactional
    public List<QuestionDto> reorderQuestions(Long formId, List<Long> questionIds, Long userId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + formId));

        if (!validationService.isUserAuthorizedToEdit(form, userId)) {
            throw new UnauthorizedException("User is not authorized to reorder questions in this form");
        }

        List<Question> questions = questionRepository.findAllById(questionIds);
        if (questions.size() != questionIds.size()) {
            throw new ResourceNotFoundException("Some questions do not exist");
        }

        if (questions.stream().anyMatch(q -> !q.getForm().getId().equals(formId))) {
            throw new IllegalArgumentException("Some questions do not belong to the specified form");
        }

        IntStream.range(0, questionIds.size()).forEach(index -> {
            Long questionId = questionIds.get(index);
            questions.stream()
                    .filter(q -> q.getId().equals(questionId))
                    .findFirst()
                    .ifPresent(q -> {
                        q.setOrderIndex(index);
                        q.setUserId(userId);
                    });
        });

        List<Question> savedQuestions = questionRepository.saveAll(questions);
        return questionMapper.toDtoList(savedQuestions);
    }

    /**
     * Checks whether the user is authorized to perform actions related to the given form.
     *
     * @param form the form for which the user's authorization is being verified
     * @param userId the unique identifier of the user
     * @return true if the user is authorized, false otherwise
     */
    private boolean isUserAuthorized(Form form, Long userId) {
        return validationService.isUserAuthorized(form, userId);
    }
}