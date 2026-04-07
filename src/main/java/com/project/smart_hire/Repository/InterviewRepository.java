package com.project.smart_hire.Repository;

import com.project.smart_hire.Entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByRecruiterId(Long recruiterId);

    Optional<Interview> findByCandidateIdAndJobIdAndType(Long candidateId, Long jobId, String mcq);
}