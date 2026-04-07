package com.project.smart_hire.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "results")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;
    private Long candidateId;
    private String type;
    private Double score;
    private String feedback;
    private LocalDateTime submittedAt;
}