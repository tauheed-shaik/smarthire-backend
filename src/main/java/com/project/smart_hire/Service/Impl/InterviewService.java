package com.project.smart_hire.Service.Impl;

import com.project.smart_hire.DTO.InterviewCreateDTO;
import com.project.smart_hire.Entity.*;
import com.project.smart_hire.Repository.*;
import com.project.smart_hire.Service.IInterviewService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InterviewService implements IInterviewService {

    @Autowired
    private InterviewRepository repo;

    @Autowired
    private CandidateRepository candidateRepo;

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private InterviewSessionRepository sessionRepo;

    @Autowired
    private CodingQuestionRepository codingRepo;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public InterviewSession scheduleInterview(InterviewCreateDTO dto, Long recruiterId) {

        // 1. Basic DTO validation
        if (dto.getJobId() == null || dto.getCandidateId() == null || dto.getType() == null) {
            throw new IllegalArgumentException("jobId, candidateId and type are required");
        }

        // 2. Validate candidate & job exist
        Candidate candidate = candidateRepo.findById(dto.getCandidateId())
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found with id: " + dto.getCandidateId()));

        Job job = jobRepo.findById(dto.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + dto.getJobId()));

        // 3. Prevent duplicate active interviews for same job + candidate
        List<String> activeStatuses = Arrays.asList("PENDING", "STARTED", "SUBMITTED");
        if (sessionRepo.existsByCandidateIdAndJobIdAndStatusIn(
                dto.getCandidateId(), dto.getJobId(), activeStatuses)) {
            throw new IllegalStateException("An active interview already exists for this candidate and job");
        }

        // 4. Create session
        InterviewSession session = new InterviewSession();
        session.setCandidateId(dto.getCandidateId());
        session.setJobId(dto.getJobId());
        session.setType(dto.getType().trim().toUpperCase());
        session.setStatus("PENDING");
        session.setScheduledAt(dto.getScheduledAt() != null ? dto.getScheduledAt() : LocalDateTime.now());
        session.setInviteLink(generateInviteLink(session));

        // 5. Type-specific logic
        String type = session.getType();

        if ("CODING".equals(type)) {
            if (dto.getQuestionIds() == null || dto.getQuestionIds().isEmpty()) {
                throw new IllegalArgumentException("CODING interview requires at least one coding question ID");
            }

            // 1. Load the questions from database (this is already there)
            List<CodingQuestion> questions = dto.getQuestionIds().stream()
                    .map(id -> codingRepo.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Coding question not found: id = " + id)))
                    .collect(Collectors.toList());

            // 2. ADD THESE 3 LINES RIGHT HERE — THIS IS THE IMPORTANT PART
            session.getCodingQuestions().clear();           // Remove old questions (makes Hibernate notice change)
            session.getCodingQuestions().addAll(questions); // Add the new ones
            // (Do NOT add anything else here)

            // 3. Save the session — this writes to database + join table
            InterviewSession saved = sessionRepo.saveAndFlush(session);

            // Optional: print for testing (you can remove later)
            System.out.println("CODING session saved! ID = " + saved.getId());
            System.out.println("Questions added: " + saved.getCodingQuestions().size());

            // Rest of your code (like return saved)
            return saved;
        } else if ("MCQ".equals(type)) {
            session.setQuestionIds(dto.getQuestionIds() != null ?
                    new ArrayList<>(dto.getQuestionIds()) :
                    new ArrayList<>());

        } else {
            throw new IllegalArgumentException("Unsupported interview type: " + type + " (only MCQ and CODING supported)");
        }

        // 6. Save and return
        InterviewSession saved = sessionRepo.save(session);
        sessionRepo.flush();           // instead of saveAndFlush
        entityManager.clear();         // sometimes helps
        entityManager.refresh(session);

            System.out.println("=== AFTER SAVE ===");
            System.out.println("Session ID: " + saved.getId());
            System.out.println("Coding questions in memory: " + saved.getCodingQuestions().size());

    // Force reload from DB to see what really got persisted
            entityManager.refresh(saved);   // ← requires @PersistenceContext EntityManager em;

            System.out.println("Coding questions AFTER REFRESH: " + saved.getCodingQuestions().size());

    // Or even better – manual query
            List<Long> idsFromJoin = entityManager.createNativeQuery(
                            "SELECT coding_question_ids FROM interview_session_coding_question_ids WHERE interview_session_id = :sid"
                    )
                    .setParameter("sid", saved.getId())
                    .getResultList();

            System.out.println("JOIN TABLE ROWS FOUND IN DB: " + idsFromJoin.size());
            System.out.println("Question IDs in join table: " + idsFromJoin);

        // Optional debug log (remove in production or use proper logger)
        System.out.println("Scheduled " + type + " interview | Session ID: " + saved.getId() +
                " | Questions: " + (type.equals("CODING") ? saved.getCodingQuestions().size() : saved.getQuestionIds().size()));

        return saved;
    }

    private String generateInviteLink(InterviewSession session) {
        return frontendUrl + "/interview?sessionId=" + session.getId() +
                "&type=" + session.getType().toLowerCase() +
                "&candidateId=" + session.getCandidateId();
    }

    @Override
    public Interview save(Interview interview) {
        Candidate candidate = candidateRepo.findById(interview.getCandidateId())
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        // FETCH JOB
        Job job = jobRepo.findById(interview.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        // POPULATE NAME & TITLE
        interview.setCandidateName(candidate.getName());
        interview.setCandidateEmail(candidate.getEmail());
        interview.setJobTitle(job.getTitle());

        return repo.save(interview);
    }

    @Override
    public List<Interview> findByRecruiterId(Long recruiterId) {
        return repo.findByRecruiterId(recruiterId);
    }

    @Override
    public String getCandidateEmail(Long candidateId) {
        return candidateRepo.findById(candidateId)
                .map(Candidate::getEmail)
                .orElseThrow();
    }

    @Override
    public String getCandidateName(Long candidateId) {
        return candidateRepo.findById(candidateId)
                .map(Candidate::getName)
                .orElseThrow();
    }

    @Override
    public String getJobTitle(Long jobId) {
        return jobRepo.findById(jobId)
                .map(Job::getTitle)
                .orElseThrow();
    }

    @Override
    public Optional<Interview> findById(Long interviewId) {
        return repo.findById(interviewId);
    }
}