package org.microservices.formservice.repository;

import org.microservices.formservice.entity.Collaborator;
import org.microservices.formservice.enums.CollaboratorRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    List<Collaborator> findByFormId(Long formId);
    boolean existsByFormIdAndUserId(Long formId, Long userId);

    void deleteByFormId(Long id);

    boolean existsByFormIdAndUserIdAndRole(Long formId, Long userId, CollaboratorRole role);

}