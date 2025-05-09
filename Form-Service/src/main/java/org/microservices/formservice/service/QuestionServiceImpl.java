package org.microservices.formservice.service;

import jakarta.ws.rs.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.QuestionDto;
import org.microservices.formservice.entity.Form;
import org.microservices.formservice.entity.Option;
import org.microservices.formservice.entity.Question;
import org.microservices.formservice.mappers.QuestionMapper;
import org.microservices.formservice.repository.CollaboratorRepository;
import org.microservices.formservice.repository.FormRepository;
import org.microservices.formservice.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final FormRepository formRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<QuestionDto> getQuestionsByForm(Long formId, Long userId) {
        // Check if the form exists
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found with id: " + formId));

        // Check if user is authorized to view this form
        if (!isUserAuthorized(form, userId)) {
            throw new NotAuthorizedException("User is not authorized to view this form's questions");
        }

        List<Question> questions = questionRepository.findByFormIdWithOptionsOrdered(formId);
        return questionMapper.toDtoList(questions);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionDto getQuestionById(Long id, Long userId) {
        Question question = questionRepository.findByIdWithOptions(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

        // Check if user is authorized to view this question
        if (!isUserAuthorized(question.getForm(), userId)) {
            throw new NotAuthorizedException("User is not authorized to view this question");
        }

        return questionMapper.toDto(question);
    }

    @Override
    @Transactional
    public QuestionDto createQuestion(Long formId, QuestionDto questionDto, Long userId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found with id: " + formId));

        // Check if user is authorized to modify this form
        if (!isUserAuthorized(form, userId)) {
            throw new NotAuthorizedException("User is not authorized to add questions to this form");
        }

        // Set the order index if not provided
        if (questionDto.getOrderIndex() == null) {
            Integer maxOrderIndex = questionRepository.findMaxOrderIndexByFormId(formId).orElse(-1);
            questionDto.setOrderIndex(maxOrderIndex + 1);
        }

        Question question = questionMapper.toEntity(questionDto);
        question.setForm(form);

        Question savedQuestion = questionRepository.save(question);
        return questionMapper.toDto(savedQuestion);
    }

    @Override
    @Transactional
    public QuestionDto updateQuestion(Long id, QuestionDto questionDto, Long userId) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

        // Check if user is authorized to modify this question
        if (!isUserAuthorized(question.getForm(), userId)) {
            throw new NotAuthorizedException("User is not authorized to update this question");
        }

        questionMapper.updateEntityFromDto(questionDto, question);
        Question updatedQuestion = questionRepository.save(question);
        return questionMapper.toDto(updatedQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id, Long userId) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

        // Check if user is authorized to delete this question
        if (!isUserAuthorized(question.getForm(), userId)) {
            throw new NotAuthorizedException("User is not authorized to delete this question");
        }

        questionRepository.delete(question);
    }

    @Override
    @Transactional
    public QuestionDto cloneQuestion(Long id, Long userId) {
        Question originalQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

        if (!isUserAuthorized(originalQuestion.getForm(), userId)) {
            throw new NotAuthorizedException("User is not authorized to clone this question");
        }

        // Create a new instance with same properties
        Question clonedQuestion = new Question();
        clonedQuestion.setForm(originalQuestion.getForm());
        clonedQuestion.setText(originalQuestion.getText());
        clonedQuestion.setType(originalQuestion.getType());
        clonedQuestion.setRequired(originalQuestion.isRequired());

        // Find max order number and set new question to be last
        Integer maxOrderNum = questionRepository.findMaxOrderNumByFormId(originalQuestion.getForm().getId());
        clonedQuestion.setOrderIndex(maxOrderNum != null ? maxOrderNum + 1 : 1);

        Question savedQuestion = questionRepository.save(clonedQuestion);

        // Clone options if any
        if (originalQuestion.getOptions() != null && !originalQuestion.getOptions().isEmpty()) {
            List<Option> clonedOptions = new ArrayList<>();
            for (Option originalOption : originalQuestion.getOptions()) {
                Option newOption = new Option();
                newOption.setText(originalOption.getText());
                newOption.setQuestion(savedQuestion);
                clonedOptions.add(newOption);
            }
            savedQuestion.setOptions(clonedOptions);
            savedQuestion = questionRepository.save(savedQuestion);
        }

        return questionMapper.toDto(savedQuestion);
    }

    @Override
    @Transactional
    public List<QuestionDto> reorderQuestions(Long formId, List<Long> questionIds, Long userId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found with id: " + formId));

        // Check if user is authorized to modify this form
        if (!isUserAuthorized(form, userId)) {
            throw new NotAuthorizedException("User is not authorized to reorder questions in this form");
        }

        // Check if all questions belong to the form
        List<Question> questions = questionRepository.findAllById(questionIds);
        if (questions.size() != questionIds.size()) {
            throw new RuntimeException("Some questions do not exist");
        }

        if (questions.stream().anyMatch(q -> !q.getForm().getId().equals(formId))) {
            throw new IllegalArgumentException("Some questions do not belong to the specified form");
        }

        // Update order indices
        IntStream.range(0, questionIds.size()).forEach(index -> {
            Long questionId = questionIds.get(index);
            questions.stream()
                    .filter(q -> q.getId().equals(questionId))
                    .findFirst()
                    .ifPresent(q -> q.setOrderIndex(index));
        });

        List<Question> savedQuestions = questionRepository.saveAll(questions);
        return questionMapper.toDtoList(savedQuestions);
    }

    private boolean isUserAuthorized(Form form, Long userId) {
        boolean isOwner = form.getCreatedBy().equals(userId);

        boolean isCollaborator = false;
        if (!isOwner) {
            isCollaborator = collaboratorRepository.existsByFormIdAndUserId(form.getId(), userId);
        }

        return isOwner || isCollaborator;
    }
}