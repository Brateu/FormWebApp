package org.microservices.formservice.service;

import org.microservices.formservice.DTO.QuestionDto;

import java.util.List;

/**
 * Service interface for managing questions within forms.
 * Provides methods to perform CRUD operations, manage question order,
 * and handle additional functionalities such as cloning questions.
 */
public interface QuestionService {


    /**
     * Retrieves a list of questions associated with a specific form and user.
     *
     * @param formId the unique identifier of the form whose questions are to be retrieved
     * @param userId the unique identifier of the user requesting the questions
     * @return a list of QuestionDto objects representing the questions associated with the specified form and user
     */
    List<QuestionDto> getQuestionsByForm(Long formId, Long userId);


    /**
     * Retrieves a specific question by its unique identifier and the associated user identifier.
     *
     * @param id the unique identifier of the question to be retrieved
     * @param userId the unique identifier of the user requesting the question
     * @return a QuestionDto object containing the question details, or null if the question
     *         is not found or not accessible to the specified user
     */
    QuestionDto getQuestionById(Long id, Long userId);


    /**
     * Creates a new question within a specified form for a specific user.
     *
     * @param formId the unique identifier of the form to which the question will be added
     * @param questionDto the data transfer object containing the details of the question to be created
     * @param userId the unique identifier of the user creating the question
     * @return the created QuestionDto object containing the details of the newly added question
     */
    QuestionDto createQuestion(Long formId, QuestionDto questionDto, Long userId);


    /**
     * Updates an existing question identified by its unique identifier.
     *
     * @param id the unique identifier of the question to be updated
     * @param questionDto the DTO containing the updated question details
     * @param userId the unique identifier of the user performing the update
     * @return the updated question as a QuestionDto object
     */
    QuestionDto updateQuestion(Long id, QuestionDto questionDto, Long userId);


    /**
     * Deletes a specific question identified by its unique identifier for a specified user.
     *
     * @param id the unique identifier of the question to be deleted
     * @param userId the unique identifier of the user requesting the deletion
     */
    void deleteQuestion(Long id, Long userId);


    /**
     * Creates a duplicate of an existing question identified by its unique identifier for a specified user.
     *
     * @param id the unique identifier of the question to be cloned
     * @param userId the unique identifier of the user requesting the clone operation
     * @return the cloned question as a QuestionDto object
     */
    QuestionDto cloneQuestion(Long id, Long userId);


    /**
     * Reorders the questions in a specific form as per the provided order.
     * The updated order of questions is persisted, and only accessible questions for the user
     * will be affected.
     *
     * @param formId the unique identifier of the form containing the questions to be reordered
     * @param questionIds the list of question IDs that defines the new order
     * @param userId the unique identifier of the user performing the reorder operation
     * @return a list of QuestionDto objects representing the questions in their new order
     */
    List<QuestionDto> reorderQuestions(Long formId, List<Long> questionIds, Long userId);
}