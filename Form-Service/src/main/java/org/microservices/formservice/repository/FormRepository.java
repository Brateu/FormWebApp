package org.microservices.formservice.repository;

import org.microservices.formservice.entity.Form;
import org.microservices.formservice.enums.Status;
import org.microservices.formservice.enums.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findByCreatedBy(long userId);
    List<Form> findByCollaborators_UserId(long userId);
    List<Form> findByStatus(Status status);
    List<Form> findByVisibility(Visibility visibility);
    List<Form> getFormsByVisibility(Visibility visibilities);
}