// src/main/java/com/project/smart_hire/Controller/RecruiterController.java
package com.project.smart_hire.Controller;

import com.project.smart_hire.Config.AppConfig;
import com.project.smart_hire.Entity.*;
import com.project.smart_hire.Repository.InterviewSessionRepository;
import com.project.smart_hire.Service.*;
import com.project.smart_hire.Service.Impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/recruiter")
public class RecruiterController {

    @Autowired
    private IRecruiterService recruiterService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private JobService jobService;
    @Autowired
    private ApplicationService appService;
    @Autowired
    private IInterviewService interviewService;
    @Autowired
    private MCQService mcqService;
    @Autowired
    private ResultService resultService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private InterviewSessionRepository sessionRepo;

    // === 1. LOGIN ===
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Recruiter> recruiter = recruiterService.loginRecruiter(email, password);
            response.put("msg", "Login successful");
            response.put("recruiterId", recruiter.get().getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // === 2. LOGOUT ===
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("msg", "Logged out successfully"));
    }

    // === 3. PROFILE ===
    @GetMapping("/profile/{recruiterId}")
    public ResponseEntity<?> getProfile(@PathVariable Long recruiterId) {
        try {
            Recruiter r = recruiterService.getRecruiterById(recruiterId).orElseThrow();
            return ResponseEntity.ok(Map.of("msg", "Profile fetched", "profile", r));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", e.getMessage()));
        }
    }

    @PutMapping("/profile/{recruiterId}")
    public ResponseEntity<?> updateProfile(@RequestBody Recruiter recruiter, @PathVariable Long recruiterId) {
        try {
            Recruiter updated = recruiterService.updateRecruiterProfile(recruiter, recruiterId);
            return ResponseEntity.ok(Map.of("msg", "Profile updated", "profile", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", e.getMessage()));
        }
    }

    // === 4. ADD COMPANY ===
    @PostMapping("/companies")
    public ResponseEntity<?> addCompany(@RequestBody Company company, @RequestParam Long recruiterId) {
        try {
            company.setRecruiterId(recruiterId);
            Company saved = companyService.save(company);
            return ResponseEntity.ok(Map.of("msg", "Company added", "companyId", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", e.getMessage()));
        }
    }

    // === 5. GET COMPANIES ===
    @GetMapping("/companies/{recruiterId}")
    public ResponseEntity<?> getCompanies(@PathVariable Long recruiterId) {
        try {
            List<Company> companies = companyService.findByRecruiterId(recruiterId);
            return ResponseEntity.ok(Map.of("msg", "Companies fetched", "companies", companies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", e.getMessage()));
        }
    }

    // === 6. ADD JOB ===
    @PostMapping("/jobs")
    public ResponseEntity<?> addJob(@RequestBody Job job, @RequestParam Long recruiterId) {
        try {
            job.setRecruiterId(recruiterId);
            Job saved = jobService.save(job);
            return ResponseEntity.ok(Map.of("msg", "Job posted", "jobId", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", e.getMessage()));
        }
    }

    // === 7. GET JOBS ===
    @GetMapping("/jobs")
    public ResponseEntity<?> getJobs(@RequestParam Long recruiterId) {
        try {
            List<Job> jobs = jobService.findByRecruiterId(recruiterId);
            return ResponseEntity.ok(Map.of("msg", "Jobs fetched", "jobs", jobs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", e.getMessage()));
        }
    }

    // === 8. VIEW RESUMES (Applications for Job) - FIXED ===
    @GetMapping("/resumes/{jobId}")
    public ResponseEntity<?> getResumes(@PathVariable Long jobId) {
        try {
            List<Application> apps = appService.findByJobId(jobId);
            List<Map<String, Object>> resumes = apps.stream()
                    .map(a -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("applicationId", a.getId());
                        map.put("candidateId", a.getCandidateId());
                        map.put("candidateName", a.getCandidateName());
                        map.put("resumePath", a.getResumePath());
                        map.put("appliedAt", a.getAppliedAt());
                        map.put("status", a.getStatus());
                        return map;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("msg", "Resumes fetched", "resumes", resumes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Error", e.getMessage()));
        }
    }

    // === 9. SCHEDULE INTERVIEW ===
    @PostMapping("/interviews")
    public ResponseEntity<?> scheduleInterview(@RequestBody Map<String, Object> request) {
        try {
            Long jobId = Long.valueOf(request.get("jobId").toString());
            Long candidateId = Long.valueOf(request.get("candidateId").toString());
            String type = request.get("type").toString().trim().toUpperCase();
            String meetingLink = (String) request.get("meetingLink");

            Interview interview = new Interview();
            interview.setJobId(jobId);
            interview.setCandidateId(candidateId);
            interview.setType(type);
            interview.setMeetingLink(meetingLink);
            interview.setStatus("PENDING");
            interview.setScheduledAt(LocalDateTime.now().plusMinutes(30));

            Interview savedInterview = interviewService.save(interview);

            String inviteLink = appConfig.getFrontend().getBaseUrl() +
                    appConfig.getFrontend().getCandidate().getInterviewPath() +
                    "?session=" + savedInterview.getId();

            // CRITICAL: CREATE MCQ SESSION HERE
            if ("MCQ".equals(type)) {
                @SuppressWarnings("unchecked")
                List<Long> questionIds = request.get("questionIds") != null
                        ? ((List<?>) request.get("questionIds")).stream()
                        .map(o -> Long.valueOf(o.toString()))
                        .collect(Collectors.toList())
                        : Collections.emptyList();

                if (questionIds.isEmpty()) {
                    throw new IllegalArgumentException("No questions selected for MCQ test");
                }

                InterviewSession session = new InterviewSession();
                session.setCandidateId(candidateId);
                session.setJobId(jobId);
                session.setType("MCQ");
                session.setStatus("PENDING");
                session.setQuestionIds(questionIds);
                session.setScheduledAt(LocalDateTime.now());
                session.setInviteLink(inviteLink);
                session.setWebcamRequired(true);

                sessionRepo.save(session);
                System.out.println("MCQ InterviewSession created for candidate " + candidateId);
            }

            emailService.sendInterviewInvite(
                    savedInterview.getCandidateEmail(),
                    savedInterview,
                    inviteLink
            );

            return ResponseEntity.ok(Map.of(
                    "msg", "Interview scheduled",
                    "sessionId", savedInterview.getId(),
                    "inviteLink", inviteLink
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Error", e.getMessage()));
        }
    }

    // === 10. GENERATE MCQs ===
    @PostMapping("/questions/generate")
    public ResponseEntity<?> generateMCQs(
            @RequestParam String topic,
            @RequestParam int count) {
        try {
            System.out.println("DEBUG: Generating MCQs for topic: " + topic + ", count: " + count);
            List<MCQ> questions = mcqService.generateMCQs(topic, count);
            System.out.println("DEBUG: Generated " + questions.size() + " questions");
            return ResponseEntity.ok(Map.of(
                    "msg", "Generated " + questions.size() + " questions",
                    "questions", questions
            ));
        } catch (Exception e) {
            System.out.println("DEBUG: Error generating MCQs: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", e.getMessage()));
        }
    }

    // === 11. SAVE MCQ ===
    @PostMapping("/mcq/save")
    public ResponseEntity<?> saveMCQ(
            @RequestBody MCQ mcq,
            @RequestParam Long recruiterId) {
        try {
            MCQ saved = mcqService.saveMCQ(mcq, recruiterId);
            return ResponseEntity.ok(Map.of("msg", "Question saved", "question", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Error", e.getMessage()));
        }
    }

    // === 12. GET ALL MCQs ===
    @GetMapping("/mcq/all")
    public ResponseEntity<?> getAllMCQs(@RequestParam Long recruiterId) {
        try {
            List<MCQ> questions = mcqService.findByRecruiterId(recruiterId);
            return ResponseEntity.ok(Map.of("questions", questions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", e.getMessage()));
        }
    }

    // === 13. UPDATE MCQ ===
    @PutMapping("/mcq/{id}")
    public ResponseEntity<?> updateMCQ(
            @PathVariable Long id,
            @RequestBody MCQ mcq,
            @RequestParam Long recruiterId) {
        try {
            MCQ updated = mcqService.updateMCQ(id, mcq);
            return ResponseEntity.ok(Map.of("msg", "Question updated", "question", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Error", e.getMessage()));
        }
    }

    // === 14. DELETE MCQ ===
    @DeleteMapping("/mcq/{id}")
    public ResponseEntity<?> deleteMCQ(
            @PathVariable Long id,
            @RequestParam Long recruiterId) {
        try {
            mcqService.deleteMCQ(id);
            return ResponseEntity.ok(Map.of("msg", "Question deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Error", e.getMessage()));
        }
    }

    // === 15. EVALUATE RESULT ===
    @GetMapping("/results/{sessionId}")
    public ResponseEntity<?> getResult(@PathVariable Long sessionId) {
        try {
            Optional<Result> result = resultService.findBySessionId(sessionId);
            return ResponseEntity.ok(Map.of("msg", "Result fetched", "result", result.orElse(null)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error", e.getMessage()));
        }
    }

    // === 16. UPDATE APPLICATION STATUS ===
    @PutMapping("/applications/{appId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long appId, @RequestParam String status) {
        try {
            Application app = appService.updateStatus(appId, status);
            return ResponseEntity.ok(Map.of("msg", "Status updated", "status", app.getStatus()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", e.getMessage()));
        }
    }

    // === 17. GET ALL INTERVIEWS ===
    @GetMapping("/interviews")
    public ResponseEntity<?> getInterviews(@RequestParam Long recruiterId) {
        try {
            List<Interview> interviews = interviewService.findByRecruiterId(recruiterId);
            return ResponseEntity.ok(Map.of("msg", "Interviews fetched", "interviews", interviews));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", e.getMessage()));
        }
    }
}