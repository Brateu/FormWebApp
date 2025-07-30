package org.microservices.formservice.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing an option for a question.
 * Options are possible choices that can be selected for multiple-choice,
 * checkbox, or other question types that offer predefined choices.
 */
@Entity
@Table(name = "options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option {

    /**
     * Unique identifier for the option.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The question this option belongs to.
     * Many options can be associated with one question.
     */
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /**
     * Text content of the option.
     */
    private String text;

    /**
     * URL to an image associated with the option, if any.
     */
    @Column(name = "image_url")
    private String imageUrl;
}