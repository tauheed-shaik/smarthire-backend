package com.project.smart_hire.Repository;

import com.project.smart_hire.Entity.MCQ;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MCQRepository extends JpaRepository<MCQ, Long> {
    List<MCQ> findByRecruiterId(Long recruiterId);
}