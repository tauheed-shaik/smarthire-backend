package com.project.smart_hire.Service.Impl;

import com.project.smart_hire.Entity.Job;
import com.project.smart_hire.Repository.JobRepository;
import com.project.smart_hire.Service.IJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService implements IJobService {

    @Autowired private JobRepository repo;

    @Override
    public Job createJob(Job job) {
        return repo.save(job);
    }

    @Override
    public List<Job> getJobsByRecruiter(Long recruiterId) {
        return repo.findByRecruiterId(recruiterId);
    }

    @Override
    public Job save(Job job) {
        return repo.save(job);
    }

    @Override
    public List<Job> findByRecruiterId(Long recruiterId) {
        return repo.findByRecruiterId(recruiterId);
    }
}