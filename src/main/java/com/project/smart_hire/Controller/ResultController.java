package com.project.smart_hire.Controller;

import com.project.smart_hire.Entity.Application;
import com.project.smart_hire.Entity.InterviewResponse;
import com.project.smart_hire.Repository.ApplicationRepository;
import com.project.smart_hire.Repository.InterviewResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/results")
@CrossOrigin("*")
public class ResultController {

    @Autowired
    private InterviewResponseRepository responseRepo;

    @Autowired
    private ApplicationRepository appRepo;

    // Candidate: Check Result
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<?> getResults(@PathVariable Long candidateId) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            List<InterviewResponse> results = responseRepo.findByCandidateId(candidateId);
            List<Application> apps = appRepo.findByCandidateId(candidateId);

            response.put("msg", "Results and status fetched");
            response.put("interviewResults", results);
            response.put("applicationStatus", apps);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Recruiter: View Candidate Result
    @GetMapping("/response/{responseId}")
    public ResponseEntity<?> getResponse(@PathVariable Long responseId) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            InterviewResponse resp = responseRepo.findById(responseId)
                    .orElseThrow(() -> new RuntimeException("Result not found"));
            response.put("msg", "Result fetched");
            response.put("result", resp);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}