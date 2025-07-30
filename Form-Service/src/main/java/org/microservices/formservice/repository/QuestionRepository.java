package org.microservices.formservice.repository;

import org.microservices.formservice.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Question entities.
 * Extends JpaRepository to provide standard methods for CRUD operations
 * and adds custom queries related to questions in forms.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Retrieves a list of questions associated with a specified form, sorted in ascending order
     * by the orderIndex field.
     *
     * @param formId the unique identifier of the form whose questions need to be retrieved
     * @return a list of questions for the given form, ordered by their order index in ascending order
     */
    List<Question> findByFormIdOrderByOrderIndexAsc(Long formId);

    /**
     * Retrieves the maximum order index of questions associated with a specific form.
     *
     * @param formId the unique identifier of the form whose maximum question order index is to be retrieved
     * @return an Optional containing the maximum order index if questions exist for the form,
     *         or an empty Optional if no questions are associated with the form
     */
    @Query("SELECT MAX(q.orderIndex) FROM Question q WHERE q.form.id = :formId")
    Optional<Integer> findMaxOrderIndexByFormId(@Param("formId") Long formId);

    /**
     * Retrieves a Question entity by its unique identifier, along with its associated options.
     * Utilizes a LEFT JOIN FETCH to eagerly load the options related to the question.
     *
     * @param id the unique identifier of the question
     * @return an Optional containing the Question entity with its options if found,
     *         or an empty Optional if no question is found with the given ID
     */
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.options WHERE q.id = :id")
    Optional<Question> findByIdWithOptions(@Param("id") Long id);

    /**
     * Retrieves the list of questions associated with the specified form ID.
     * Each question includes its options loaded, and the results are ordered by the question's order index.
     *
     * @param formId The unique identifier of the form whose questions are to be retrieved.
     * @return A list of questions associated with the specified form, including their options, ordered by their order index.
     */
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.options WHERE q.form.id = :formId ORDER BY q.orderIndex")
    List<Question> findByFormIdWithOptionsOrdered(@Param("formId") Long formId);

    /**
     * Checks if a question exists by its associated form ID and question ID.
     *
     * @param formId The ID of the form to which the question belongs.
     * @param questionId The ID of the question to check.
     * @return true if a question exists with the given form ID and question ID, false otherwise.
     */
    boolean existsByFormIdAndId(Long formId, Long questionId);

    /**
     * Deletes all elements associated with the given form ID.
     *
     * @param formId the unique identifier of the form whose associated elements need to be deleted
     */
    void deleteByFormId(Long formId);

    /**
     * Finds the maximum order number of questions associated with a specific form.
     *
     * @param id The ID of the form whose maximum question order number needs to be retrieved.
     * @return The maximum order number of the questions in the specified form,
     *         or null if the form has no associated questions.
     */
    Integer findMaxOrderNumByFormId(Long id);
}