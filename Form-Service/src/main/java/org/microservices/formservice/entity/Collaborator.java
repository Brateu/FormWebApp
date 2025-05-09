package org.microservices.formservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.microservices.formservice.enums.CollaboratorRole;

@Entity
@Table(name = "collaborators")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "form_id", nullable = false)
    @JsonBackReference
    private Form form;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private CollaboratorRole role;  // VIEWER, EDITOR
}