package org.microservices.formservice.service;

import org.microservices.formservice.DTO.CollaboratorDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CollaboratorService {
    List<CollaboratorDto> getCollaborators(Long formId);
    CollaboratorDto addCollaborator(Long formId, CollaboratorDto dto);
    void removeCollaborator(Long id);
}