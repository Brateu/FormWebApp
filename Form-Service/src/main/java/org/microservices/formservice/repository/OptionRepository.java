package org.microservices.formservice.repository;

import org.microservices.formservice.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Option entities. Provides methods for
 * performing CRUD operations and querying options based on their associated question.
 */
@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    /**
     * Retrieves a list of options associated with the specified question ID.
     *
     * @param questionId the ID of the question for which to retrieve associated options
     * @return a list of options belonging to the specified question, or an empty list if no options are found
     */
    List<Option> findByQuestionId(Long questionId);
}