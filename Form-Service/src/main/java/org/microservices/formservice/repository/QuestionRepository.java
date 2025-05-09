package org.microservices.formservice.repository;

import org.microservices.formservice.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByFormIdOrderByOrderIndexAsc(Long formId);

    @Query("SELECT MAX(q.orderIndex) FROM Question q WHERE q.form.id = :formId")
    Optional<Integer> findMaxOrderIndexByFormId(@Param("formId") Long formId);

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.options WHERE q.id = :id")
    Optional<Question> findByIdWithOptions(@Param("id") Long id);

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.options WHERE q.form.id = :formId ORDER BY q.orderIndex")
    List<Question> findByFormIdWithOptionsOrdered(@Param("formId") Long formId);

    boolean existsByFormIdAndId(Long formId, Long questionId);

    void deleteByFormId(Long formId);

    Integer findMaxOrderNumByFormId(Long id);
}