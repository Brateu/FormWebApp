package org.microservices.formservice.service;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.CollaboratorDto;
import org.springframework.stereotype.Service;
import org.microservices.formservice.repository.CollaboratorRepository;
import org.microservices.formservice.repository.FormRepository;
import org.microservices.formservice.mappers.CollaboratorMapper;
import org.microservices.formservice.entity.Collaborator;
import org.microservices.formservice.entity.Form;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollaboratorServiceImpl implements CollaboratorService {
    private final CollaboratorRepository collaboratorRepository;
    private final FormRepository formRepository;
    private final CollaboratorMapper collaboratorMapper;

    @Override
    public List<CollaboratorDto> getCollaborators(Long formId) {
        return collaboratorRepository.findByFormId(formId)
                .stream()
                .map(collaboratorMapper::toDto)
                .toList();
    }

    @Override
    public CollaboratorDto addCollaborator(Long formId, CollaboratorDto dto) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));

        Collaborator collaborator = collaboratorMapper.toEntity(dto);
        collaborator.setForm(form);

        return collaboratorMapper.toDto(collaboratorRepository.save(collaborator));
    }

    @Override
    public void removeCollaborator(Long id) {
        collaboratorRepository.deleteById(id);
    }
}