package org.microservices.formservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microservices.formservice.enums.CollaboratorRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollaboratorDto {
    private Long id;
    private Long userId;
    private CollaboratorRole role; // VIEWER ili EDITOR
    private Long formId;
}