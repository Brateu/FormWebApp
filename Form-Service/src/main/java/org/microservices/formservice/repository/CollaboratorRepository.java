package org.microservices.formservice.repository;

import org.microservices.formservice.entity.Collaborator;
import org.microservices.formservice.enums.CollaboratorRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Collaborator entities.
 * Extends JpaRepository to provide standard CRUD operations for Collaborator objects.
 * Contains additional query methods specific to Collaborator entities.
 */
@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    /**
     * Retrieves a list of collaborators associated with the specified form ID.
     *
     * @param formId the ID of the form for which to retrieve associated collaborators
     * @return a list of collaborators linked to the specified form, or an empty list if no collaborators are found
     */
    List<Collaborator> findByFormId(Long formId);
    /**
     * Checks if a collaborator exists for the specified form ID and user ID.
     *
     * @param formId the ID of the form to check for collaboration
     * @param userId the ID of the user whose collaboration is to be verified
     * @return true if a collaborator exists for the specified form ID and user ID, otherwise false
     */
    boolean existsByFormIdAndUserId(Long formId, Long userId);

    /**
     * Deletes all collaborators associated with the specified form ID.
     *
     * @param id the ID of the form whose collaborators are to be deleted
     */
    void deleteByFormId(Long id);

    /**
     * Checks if a collaborator with a specified role exists for the given form ID and user ID.
     *
     * @param formId the ID of the form to check for collaboration
     * @param userId the ID of the user whose collaboration is to be verified
     * @param role   the role of the collaborator (e.g., VIEWER, EDITOR) to be checked
     * @return true if a collaborator with the specified role exists for the given form ID and user ID, otherwise false
     */
    boolean existsByFormIdAndUserIdAndRole(Long formId, Long userId, CollaboratorRole role);

}