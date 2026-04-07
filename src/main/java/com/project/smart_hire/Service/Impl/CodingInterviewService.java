package com.project.smart_hire.Service.Impl;

import com.project.smart_hire.DTO.InterviewCreateDTO;
import com.project.smart_hire.Entity.Candidate;
import com.project.smart_hire.Entity.CodingQuestion;
import com.project.smart_hire.Entity.InterviewSession;
import com.project.smart_hire.Entity.Job;
import com.project.smart_hire.Repository.CandidateRepository;
import com.project.smart_hire.Repository.CodingQuestionRepository;
import com.project.smart_hire.Repository.InterviewSessionRepository;
import com.project.smart_hire.Repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CodingInterviewService {

    @Autowired
    private InterviewSessionRepository sessionRepo;

    @Autowired
    private CodingQuestionRepository codingRepo;

    @Autowired
    private CandidateRepository candidateRepo;

    @Autowired
    private JobRepository jobRepo;

    @Transactional
    public InterviewSession scheduleCodingRound(InterviewCreateDTO dto, Long recruiterId) {

        if (dto.getQuestionIds() == null || dto.getQuestionIds().isEmpty()) {
            throw new IllegalArgumentException("At least one coding question is required");
        }

        Candidate candidate = candidateRepo.findById(dto.getCandidateId())
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        Job job = jobRepo.findById(dto.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        // Check no active coding round already exists for this job + candidate
        boolean exists = sessionRepo.existsByCandidateIdAndJobIdAndTypeAndStatusIn(
                dto.getCandidateId(),
                dto.getJobId(),
                "CODING",
                List.of("PENDING", "STARTED", "SUBMITTED")
        );
        if (exists) {
            throw new IllegalStateException("Active coding round already exists for this job and candidate");
        }

        InterviewSession session = new InterviewSession();
        session.setCandidateId(dto.getCandidateId());
        session.setJobId(dto.getJobId());
        session.setType("CODING");
        session.setStatus("PENDING");
        session.setScheduledAt(dto.getScheduledAt() != null ? dto.getScheduledAt() : LocalDateTime.now());

        // Load questions (managed entities)
        List<CodingQuestion> questions = codingRepo.findAllById(dto.getQuestionIds());
        if (questions.size() != dto.getQuestionIds().size()) {
            throw new IllegalArgumentException("Some coding questions were not found");
        }

        session.setCodingQuestions(questions);

        return sessionRepo.saveAndFlush(session);
    }
}
