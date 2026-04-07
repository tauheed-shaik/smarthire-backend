package com.project.smart_hire.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interview_sessions")
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long candidateId;
    private Long jobId;

    private String type; // MCQ, CODING, HR

    private String status = "PENDING"; // PENDING, STARTED, SUBMITTED, COMPLETED

    private Boolean webcamRequired = false;

    private LocalDateTime scheduledAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private String inviteLink;

    @ElementCollection
    private List<Long> questionIds = new ArrayList<>(); // ← for MCQ / HR questions

    private String meetingLink;

    // InterviewSession.java
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "interview_session_coding_question_ids",
            joinColumns = @JoinColumn(name = "interview_session_id"),
            inverseJoinColumns = @JoinColumn(name = "coding_question_ids")
    )
    private List<CodingQuestion> codingQuestions = new ArrayList<>();
}