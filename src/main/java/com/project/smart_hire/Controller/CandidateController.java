package com.project.smart_hire.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.smart_hire.Entity.Candidate;
import com.project.smart_hire.Repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/candidate")
@CrossOrigin(origins = {"https://smarthire-frontend-dk2k.vercel.app", "http://localhost:5173"})
public class CandidateController {

    @Autowired
    private CandidateRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Candidate candidate) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (repo.findByEmail(candidate.getEmail()).isPresent()) {
                response.put("Error", "Email already registered");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            candidate.setPassword(encoder.encode(candidate.getPassword()));
            Candidate saved = repo.save(candidate);
            response.put("msg", "Registered successfully");
            response.put("candidate", saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam(required = false) String email, @RequestParam(required = false) String password) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            System.out.println("=== LOGIN ATTEMPT ===");
            System.out.println("Email: " + email);
            System.out.println("Password provided: " + (password != null && !password.isEmpty()));
            
            if (email == null || email.trim().isEmpty()) {
                response.put("Error", "Email is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            if (password == null || password.trim().isEmpty()) {
                response.put("Error", "Password is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            Optional<Candidate> opt = repo.findByEmail(email.trim());
            if (opt.isEmpty()) {
                response.put("Error", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (!encoder.matches(password, opt.get().getPassword())) {
                response.put("Error", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            response.put("msg", "Login successful");
            response.put("candidateId", opt.get().getId());
            System.out.println("LOGIN SUCCESS for: " + email);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            System.err.println("LOGIN ERROR: " + e.getMessage());
            e.printStackTrace();
            response.put("Error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProfile(@RequestBody Candidate candidate, @PathVariable Long id) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Candidate existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
            existing.setName(candidate.getName());
            existing.setPhone(candidate.getPhone());
            if (candidate.getPassword() != null && !candidate.getPassword().isEmpty()) {
                existing.setPassword(encoder.encode(candidate.getPassword()));
            }
            Candidate updated = repo.save(existing);
            response.put("msg", "Profile updated");
            response.put("candidate", updated);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam Long candidateId,
            @RequestBody Map<String, Object> data) {

        HashMap<String, Object> res = new HashMap<>();

        try {
            Candidate c = repo.findById(candidateId)
                    .orElseThrow(() -> new RuntimeException("Candidate not found"));

            c.setName((String) data.get("name"));
            c.setEmail((String) data.get("email"));
            c.setPhone((String) data.get("phone"));

            List<String> skills = (List<String>) data.get("skills");
            c.setSkills(new ObjectMapper().writeValueAsString(skills));

            c.setExperience((String) data.get("experience"));
            c.setEducation((String) data.get("education"));

            repo.save(c);

            res.put("msg", "Profile updated successfully");
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            res.put("Error", e.getMessage());
            return ResponseEntity.status(400).body(res);
        }
    }

    @GetMapping("/profile/{candidateId}")
    public ResponseEntity<?> getProfile(@PathVariable Long candidateId) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Candidate c = repo.findById(candidateId)
                    .orElseThrow(() -> new RuntimeException("Candidate not found"));

            response.put("msg", "Candidate profile fetched successfully");
            response.put("Candidate profile", c);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint(){
        HashMap<String,Object> response = new HashMap<>();
        response.put("status","SUCCESS");
        response.put("message","Candidate controller is working");
        response.put("timestamp",System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all-candidates")
    public ResponseEntity<?> getAllTheCandidates(){
        HashMap<String,Object> response = new HashMap<>();
        try{
            List<Candidate> allCandidates = repo.findAll();
            response.put("msg","Candidates fetched successfully");
            response.put("candidates",allCandidates);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (Exception e){
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}