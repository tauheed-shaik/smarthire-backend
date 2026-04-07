package com.project.smart_hire.Repository;

import com.project.smart_hire.Entity.CodingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodingQuestionRepository extends JpaRepository<CodingQuestion, Long> {
    List<CodingQuestion> findByRecruiterId(Long recruiterId);
}
