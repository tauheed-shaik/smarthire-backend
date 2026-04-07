package com.project.smart_hire.Service;

import com.project.smart_hire.Entity.Application;
import java.util.List;

public interface IApplicationService {
    List<Application> getApplicationsByJob(Long jobId);
    Application updateStatus(Long appId, String status);

    List<Application> findByJobId(Long jobId);

    List<Application> findTheResumes();
}