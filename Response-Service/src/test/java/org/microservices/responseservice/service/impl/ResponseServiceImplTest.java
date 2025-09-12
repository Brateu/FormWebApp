package org.microservices.responseservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.microservices.responseservice.dto.AnsweredQuestionDto;
import org.microservices.responseservice.dto.ResponseDto;
import org.microservices.responseservice.mappers.ResponseMapper;
import org.microservices.responseservice.mappers.ResponseMapperTestImpl;
import org.microservices.responseservice.model.Response;
import org.microservices.responseservice.repository.ResponseRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResponseServiceImplTest {

    private ResponseRepository responseRepository;
    private MongoTemplate mongoTemplate;
    private ValidationService validationService;
    private ResponseServiceImpl service;
    private ResponseMapper responseMapper;

    @BeforeEach
    void setUp() {
        responseRepository = mock(ResponseRepository.class);
        mongoTemplate = mock(MongoTemplate.class);
        validationService = mock(ValidationService.class);
        responseMapper = new ResponseMapperTestImpl();
        service = new ResponseServiceImpl(responseRepository, mongoTemplate, validationService, responseMapper);
    }

    private ResponseDto sampleDto() {
        ResponseDto dto = new ResponseDto();
        dto.setFormId(1L);
        dto.setUserId(10L);
        dto.setStatus("SUBMITTED");
        AnsweredQuestionDto a1 = new AnsweredQuestionDto();
        a1.setQuestionId("q1");
        a1.setType("TEXT");
        a1.setValue("hello");
        AnsweredQuestionDto a2 = new AnsweredQuestionDto();
        a2.setQuestionId("q2");
        a2.setType("NUMBER");
        a2.setValue(5);
        dto.setAnsweredQuestions(List.of(a1, a2));
        return dto;
    }

    @Test
    void createResponse_mapsAnsweredQuestions_to_responseData_and_callsValidation() {
        ResponseDto dto = sampleDto();

        // Capture saved entity to inspect responseData mapping
        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);

        // mock save to return entity with id and fields copied
        when(responseRepository.save(any(Response.class))).thenAnswer(inv -> {
            Response r = inv.getArgument(0);
            r.setId("id-1");
            return r;
        });

        ResponseDto saved = service.createResponse(dto);

        // validate that validation was called
        verify(validationService, times(1)).validateResponse(dto);

        // verify save was called and responseData mapped
        verify(responseRepository).save(captor.capture());
        Response savedEntity = captor.getValue();
        assertNotNull(savedEntity.getResponseData());
        assertEquals("hello", savedEntity.getResponseData().get("q1"));
        assertEquals(5, savedEntity.getResponseData().get("q2"));

        // returned dto should have IDs and metadata, answeredQuestions intentionally null
        assertEquals("id-1", saved.getId());
        assertNull(saved.getAnsweredQuestions());
        assertEquals("SUBMITTED", saved.getStatus());
        assertEquals(1L, saved.getFormId());
    }

    @Test
    void submitResponse_sets_status_and_submittedAt() {
        // Prepare existing response
        Response existing = new Response();
        existing.setId("id-2");
        existing.setFormId(1L);
        existing.setUserId(10L);
        existing.setStatus("DRAFT");
        Map<String, Object> data = new HashMap<>();
        data.put("q1", "hello");
        existing.setResponseData(data);
        existing.setCreatedAt(LocalDateTime.now().minusHours(1));
        existing.setUpdatedAt(LocalDateTime.now().minusMinutes(30));

        when(responseRepository.findById("id-2")).thenReturn(Optional.of(existing));
        when(responseRepository.save(any(Response.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseDto out = service.submitResponse("id-2");

        assertEquals("SUBMITTED", out.getStatus());
        assertNotNull(out.getSubmittedAt());
        // ensure mapping still returns null for answeredQuestions
        assertNull(out.getAnsweredQuestions());
    }
}
