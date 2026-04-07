package com.project.smart_hire.Controller;

import com.project.smart_hire.Entity.Application;
import com.project.smart_hire.Repository.ApplicationRepository;
import com.project.smart_hire.Repository.CandidateRepository;
import com.project.smart_hire.Repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin("*")
public class ApplicationController {

    @Autowired
    private ApplicationRepository appRepo;

    @Autowired
    private CandidateRepository candidateRepo;

    @Autowired
    private JobRepository jobRepo;

    // Candidate applies to job
    @PostMapping("/apply")
    public ResponseEntity<?> applyForJob(
            @RequestParam Long candidateId,
            @RequestParam Long jobId) {

        HashMap<String, Object> response = new HashMap<>();

        try {
            if (candidateRepo.findById(candidateId).isEmpty()) {
                response.put("Error", "Candidate not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            if (jobRepo.findById(jobId).isEmpty()) {
                response.put("Error", "Job not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Application app = new Application();
            app.setCandidateId(candidateId);
            app.setJobId(jobId);
            Application saved = appRepo.save(app);

            response.put("msg", "Applied successfully");
            response.put("application", saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Candidate: View applied jobs
    @GetMapping("/my-applications/{candidateId}")
    public ResponseEntity<?> getMyApplications(@PathVariable Long candidateId) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Application> apps = appRepo.findByCandidateId(candidateId);
            response.put("msg", "Applications fetched");
            response.put("applications", apps);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Recruiter: View applications for a job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getApplicationsForJob(@PathVariable Long jobId) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Application> apps = appRepo.findByJobId(jobId);
            response.put("msg", "Applications for job fetched");
            response.put("applications", apps);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}