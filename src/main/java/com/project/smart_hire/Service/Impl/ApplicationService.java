package com.project.smart_hire.Service.Impl;

import com.project.smart_hire.Entity.Application;
import com.project.smart_hire.Repository.ApplicationRepository;
import com.project.smart_hire.Service.IApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService implements IApplicationService {

    @Autowired private ApplicationRepository repo;

    @Override
    public List<Application> getApplicationsByJob(Long jobId) {
        return repo.findByJobId(jobId);
    }

    @Override
    public Application updateStatus(Long appId, String status) {
        Application app = repo.findById(appId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(status);
        return repo.save(app);
    }

    @Override
    public List<Application> findByJobId(Long jobId) {
        return repo.findByJobId(jobId);
    }

    @Override
    public List<Application> findTheResumes() {
        return repo.findAll();
    }
}