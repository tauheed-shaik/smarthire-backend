package com.project.smart_hire.Repository;

import com.project.smart_hire.Entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findBySessionId(Long sessionId);
}