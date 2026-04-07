package com.project.smart_hire.Repository;

import com.project.smart_hire.Entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByActiveTrue();
    List<Job> findByRecruiterId(Long recruiterId);
}