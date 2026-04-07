package com.project.smart_hire.Entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mcqs")
public class MCQ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer; // A, B, C, D

    private Long recruiterId;
}