package org.microservices.formservice.DTO;

import lombok.*;
import org.microservices.formservice.enums.Visibility;
import org.microservices.formservice.enums.Status;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormDto {
    private Long id;
    private String name;
    private String description;
    private boolean allowAnonymous;
    private int responseLimit;
    private boolean locked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Status status;
    private Visibility visibility;
}