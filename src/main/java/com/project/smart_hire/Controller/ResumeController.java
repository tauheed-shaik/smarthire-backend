package com.project.smart_hire.Controller;

import com.project.smart_hire.Entity.Candidate;
import com.project.smart_hire.Repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = {"https://smarthire-frontend-dk2k.vercel.app", "http://localhost:5173"})
public class ResumeController {

    @Autowired
    private CandidateRepository candidateRepo;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("candidateId") Long candidateId) {

        HashMap<String, Object> response = new HashMap<>();

        try {
            Optional<Candidate> optCandidate = candidateRepo.findById(candidateId);
            if (optCandidate.isEmpty()) {
                response.put("Error", "Candidate not found with id: " + candidateId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Save file
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String uploadDir = "uploads/resumes/";
            java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + fileName);
            java.nio.file.Files.createDirectories(path.getParent());
            file.transferTo(path);

            // Save path to DB (NO GEMINI)
            Candidate candidate = optCandidate.get();
            candidate.setResumePath(uploadDir + fileName);
            candidateRepo.save(candidate);

            response.put("msg", "Resume uploaded successfully");
            response.put("filePath", uploadDir + fileName);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}