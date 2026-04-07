package com.project.smart_hire.Repository;

import com.project.smart_hire.Entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByEmail(String email);
    Optional<Candidate> findById(Long id);
}