package org.microservices.formservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.microservices.formservice.enums.Visibility;
import org.microservices.formservice.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "forms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    @Column(name = "allow_anonymous")
    private boolean allowAnonymous;

    @Column(nullable = false)
    private int responseLimit = 0;

    @Column(name = "is_locked")
    private boolean locked;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;

    private Status status;

    private Visibility visibility;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Question> questions;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Collaborator> collaborators;

}