package com.project.smart_hire.Repository;

import com.project.smart_hire.Entity.InterviewSessionCodingQuestionIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewSessionCodingQuestionIdsRepository extends JpaRepository<InterviewSessionCodingQuestionIds, Long> {

    @Query("SELECT i.codingQuestionIds FROM InterviewSessionCodingQuestionIds i WHERE i.interviewSessionId = :sessionId")
    List<Long> findCodingQuestionIdsBySessionId(Long sessionId);
}