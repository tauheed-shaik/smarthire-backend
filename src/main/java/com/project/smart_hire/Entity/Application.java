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
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long candidateId;
    private String candidateName;
    private String resumePath;
    private Long jobId;
    private LocalDateTime appliedAt = LocalDateTime.now();
    private String status = "APPLIED"; // APPLIED, SHORTLISTED, INTERVIEW, SELECTED, REJECTED
}