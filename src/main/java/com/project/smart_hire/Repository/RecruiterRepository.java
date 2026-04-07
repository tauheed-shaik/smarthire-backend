package com.project.smart_hire.Repository;

import com.project.smart_hire.Entity.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {
    Optional<Recruiter> findByEmail(String email);
}