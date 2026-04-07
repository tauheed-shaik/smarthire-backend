package com.project.smart_hire.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interviews")
public class Interview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;
    private Long candidateId;
    private String candidateEmail;
    private String type; // MCQ, CODING, HR
    private Integer duration;
    private LocalDateTime scheduledAt;
    private String status = "PENDING";

    @ElementCollection
    private List<Long> questionIds;

    private Long recruiterId;

    @Transient
    private String candidateName;

    @Transient
    private String jobTitle;

    private String meetingLink;
}