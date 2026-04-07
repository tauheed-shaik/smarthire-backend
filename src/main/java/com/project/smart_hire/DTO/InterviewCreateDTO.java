package com.project.smart_hire.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewCreateDTO {
    private Long jobId;
    private Long candidateId;
    private String candidateEmail;
    private String type;
    private Integer duration;
    private LocalDateTime scheduledAt;
    private List<Long> questionIds;
}