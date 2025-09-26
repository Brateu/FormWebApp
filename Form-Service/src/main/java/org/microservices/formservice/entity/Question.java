package org.microservices.formservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microservices.formservice.enums.QuestionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a question in a form.
 * Questions are the core components of a form that users respond to.
 * Each question has a type that determines how it is displayed and answered.
 */
@Entity
@Table(name = "questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    /**
     * Unique identifier for the question.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The text content of the question.
     */
    @Column(nullable = false)
    private String text;

    /**
     * The type of question (e.g., TEXT, MULTIPLE_CHOICE, etc.).
     * Determines how the question is displayed and answered.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionType type;

    /**
     * Flag indicating whether the question must be answered.
     */
    @Column(name = "is_required")
    private boolean required;

    /**
     * The position/order of the question within the form.
     * Default is 0.
     */
    @Column(nullable = false)
    private Integer orderIndex = 0;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;
    /**
     * ID of the user who created or last modified the question.
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * The form this question belongs to.
     * Many questions can be associated with one form.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    /**
     * List of options available for this question.
     * Applicable for question types like MULTIPLE_CHOICE, CHECKBOX, etc.
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    /**
     * Adds an option to this question and sets the bidirectional relationship.
     * 
     * @param option The option to add
     */
    public void addOption(Option option) {
        options.add(option);
        option.setQuestion(this);
    }

    /**
     * Removes an option from this question and clears the bidirectional relationship.
     * 
     * @param option The option to remove
     */
    public void removeOption(Option option) {
        options.remove(option);
        option.setQuestion(null);
    }
}