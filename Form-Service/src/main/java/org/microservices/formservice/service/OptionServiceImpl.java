package org.microservices.formservice.service;

import lombok.RequiredArgsConstructor;
import org.microservices.formservice.DTO.OptionDto;
import org.microservices.formservice.entity.Option;
import org.microservices.formservice.entity.Question;
import org.microservices.formservice.mappers.OptionMapper;
import org.microservices.formservice.repository.OptionRepository;
import org.microservices.formservice.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;
    private final OptionMapper optionMapper;

    @Override
    public List<OptionDto> getOptionsByQuestion(Long questionId) {
        return optionRepository.findByQuestionId(questionId)
                .stream()
                .map(optionMapper::toDto)
                .toList();
    }

    @Override
    public OptionDto createOption(Long questionId, OptionDto dto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Option option = optionMapper.toEntity(dto);
        option.setQuestion(question);

        return optionMapper.toDto(optionRepository.save(option));
    }

    @Override
    public OptionDto updateOption(Long id, OptionDto dto) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option not found"));

        option.setText(dto.getText());
        option.setImageUrl(dto.getImageUrl());

        return optionMapper.toDto(optionRepository.save(option));
    }

    @Override
    public void deleteOption(Long id) {
        optionRepository.deleteById(id);
    }
}