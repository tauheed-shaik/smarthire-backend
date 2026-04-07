package com.project.smart_hire.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "interview_session_coding_question_ids")
@Data
public class InterviewSessionCodingQuestionIds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interview_session_id")
    private Long interviewSessionId;

    @Column(name = "coding_question_ids")
    private Long codingQuestionIds;
}