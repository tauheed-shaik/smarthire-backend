package com.project.smart_hire.Repository;

import com.project.smart_hire.Entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {

    List<InterviewSession> findByCandidateIdAndTypeAndStatusIn(
            Long candidateId,
            String type,
            List<String> statuses
    );

    List<InterviewSession> findByCandidateIdAndTypeIgnoreCaseAndStatusIn(
            Long candidateId,
            String type,
            List<String> statuses
    );

    boolean existsByCandidateIdAndJobIdAndStatusIn(
            Long candidateId,
            Long jobId,
            List<String> statuses
    );

    List<InterviewSession> findByCandidateId(Long candidateId);

    boolean existsByCandidateIdAndJobIdAndTypeAndStatusIn(Long candidateId, Long jobId, String coding, List<String> pending);

    // Optional: keep only if you really need it
    // List<InterviewSession> findByCandidateIdAndType(Long candidateId, String type);
}