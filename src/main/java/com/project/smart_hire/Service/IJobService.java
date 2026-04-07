package com.project.smart_hire.Service;

import com.project.smart_hire.Entity.Job;
import java.util.List;

public interface IJobService {
    Job createJob(Job job);
    List<Job> getJobsByRecruiter(Long recruiterId);

    Job save(Job job);

    List<Job> findByRecruiterId(Long recruiterId);
}