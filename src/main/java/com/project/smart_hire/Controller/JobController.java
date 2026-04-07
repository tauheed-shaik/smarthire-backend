package com.project.smart_hire.Controller;

import com.project.smart_hire.Entity.Job;
import com.project.smart_hire.Repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin("*")
public class JobController {

    @Autowired
    private JobRepository jobRepo;

    @PostMapping("/post")
    public ResponseEntity<?> postJob(@RequestBody Job job) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Job savedJob = jobRepo.save(job);
            response.put("msg", "Job posted successfully");
            response.put("job", savedJob);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllJobs() {
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<Job> jobs = jobRepo.findByActiveTrue();
            response.put("msg", "Jobs fetched successfully");
            response.put("jobs", jobs);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Job job = jobRepo.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
            response.put("msg", "Job fetched successfully");
            response.put("job", job);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}