package org.microservices.formservice.repository;

import org.microservices.formservice.entity.Form;
import org.microservices.formservice.enums.CollaboratorRole;
import org.microservices.formservice.enums.Status;
import org.microservices.formservice.enums.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@code Form} entities. Provides methods for
 * performing CRUD operations and querying forms based on various attributes.
 */
@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    /**
     * Retrieves a list of forms created by the specified user.
     *
     * @param userId the ID of the user who created the forms
     * @return a list of forms created by the specified user
     */
    List<Form> findByCreatedBy(long userId);
    /**
     * Retrieves a list of forms associated with a specific user as a collaborator.
     *
     * @param userId the ID of the user who is a collaborator on the forms
     * @return a list of forms associated with the specified user as a collaborator
     */
    List<Form> findByCollaborators_UserId(long userId);
    /**
     * Retrieves a list of forms that have a specific status.
     *
     * @param status the status of the forms to be retrieved
     * @return a list of forms matching the specified status
     */
    List<Form> findByStatus(Status status);
    /**
     * Retrieves a list of forms based on their visibility setting.
     *
     * @param visibility the visibility setting of the forms (e.g., PUBLIC, PRIVATE)
     * @return a list of forms that match the specified visibility setting
     */
    List<Form> findByVisibility(Visibility visibility);
    /**
     * Retrieves a list of forms where the anonymous responses are allowed.
     *
     * @return a list of forms with the allowAnonymous flag set to true
     */
    List<Form> findByAllowAnonymousTrue();

    @Query("SELECT f.createdBy FROM Form f WHERE f.id = :formId")
    Optional<Long> findOwnerIdByFormId(@Param("formId") Long formId);

    @Query("SELECT f.visibility FROM Form f WHERE f.id = :formId")
    Optional<Visibility> findVisibilityByFormId(@Param("formId") Long formId);

    @Query("SELECT f.allowAnonymous FROM Form f WHERE f.id = :formId")
    Optional<Boolean> findAllowAnonymousByFormId(@Param("formId") Long formId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Collaborator c WHERE c.form.id = :formId AND c.userId = :userId AND c.role = :role")
    boolean existsByFormIdAndUserIdAndRole(@Param("formId") Long formId, @Param("userId") Long userId, @Param("role") CollaboratorRole role);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Collaborator c WHERE c.form.id = :formId AND c.userId = :userId")
    boolean existsByFormIdAndUserId(@Param("formId") Long formId, @Param("userId") Long userId);
}