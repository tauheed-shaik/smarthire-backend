package com.project.smart_hire.Service;

import com.project.smart_hire.Entity.Candidate;

import java.util.Optional;

public interface CandidateService {
    Optional<Candidate> getCandidateById(Long id);
}
