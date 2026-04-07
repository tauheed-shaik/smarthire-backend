package com.project.smart_hire.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class InterviewResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;
    private Long candidateId;
    private String type; // MCQ, CODING, HR
    private String questionsJson;   // From Gemini
    private String answersJson;     // Candidate's answers
    private String codeSnippet;     // For CODING
    private String videoUrl;        // For HR (optional)
    private Double score;           // Auto-calculated
    private String feedback;        // AI or Manual
    private LocalDateTime submittedAt;
    private String status = "SUBMITTED"; // SUBMITTED, EVALUATED
    private String code;        // for CODING
}