package com.project.smart_hire.Service;

import com.project.smart_hire.DTO.InterviewCreateDTO;
import com.project.smart_hire.Entity.Interview;
import com.project.smart_hire.Entity.InterviewSession;

import java.util.List;
import java.util.Optional;

public interface IInterviewService {
    InterviewSession scheduleInterview(InterviewCreateDTO dto, Long recruiterId);
    Interview save(Interview interview);
    List<Interview> findByRecruiterId(Long recruiterId);

    // NEW helpers
    String getCandidateEmail(Long candidateId);
    String getCandidateName(Long candidateId);
    String getJobTitle(Long jobId);

    Optional<Interview> findById(Long interviewId);
}