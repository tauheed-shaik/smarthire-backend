// FULL FINAL InterviewController.java – WORKS WITH YOUR CURRENT ENTITIES
package com.project.smart_hire.Controller;

import com.google.gson.Gson;
import com.project.smart_hire.DTO.InterviewCreateDTO;
import com.project.smart_hire.Entity.*;
import com.project.smart_hire.Repository.*;
import com.project.smart_hire.Service.IInterviewService;
import com.project.smart_hire.Service.Impl.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/interviews")
@CrossOrigin(origins = {"https://smarthire-frontend-dk2k.vercel.app", "http://localhost:5173"})
public class InterviewController {

    @Autowired
    private InterviewSessionRepository sessionRepo;

//    @Autowired
//    private InterviewSessionCodingQuestionIdsRepository interviewSessionCodingQuestionIdsRepository;

    @Autowired
    private CandidateRepository candidateRepo;

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private MCQRepository mcqRepo;

    @Autowired
    private InterviewResponseRepository responseRepo;

    @Autowired
    private CodingQuestionRepository codingRepo;

    @Autowired
    private IInterviewService interviewSessionService;

    // CANDIDATE: GET MCQ TEST (AppliedJobs.jsx uses this)
//    @GetMapping("/candidate/mcq/{candidateId}")
//    public ResponseEntity<?> getCandidateMCQTest(@PathVariable Long candidateId) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            Optional<InterviewSession> sessionOpt = sessionRepo.findByCandidateIdAndTypeAndStatusIn(
//                    candidateId, "MCQ", Arrays.asList("PENDING", "STARTED"));
//
//            if (sessionOpt.isEmpty()) {
//                response.put("hasMCQ", false);
//                return ResponseEntity.ok(response);
//            }
//
//            InterviewSession session = sessionOpt.get();
//
//            if (session.getQuestionIds() == null || session.getQuestionIds().isEmpty()) {
//                response.put("hasMCQ", false);
//                return ResponseEntity.ok(response);
//            }
//
//            List<MCQ> questions = mcqRepo.findAllById(session.getQuestionIds());
//
//            response.put("hasMCQ", true);
//            response.put("sessionId", session.getId());
//            response.put("jobId", session.getJobId());
//            response.put("questions", questions);
//            response.put("timeLimit", 1800);
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.put("hasMCQ", false);
//            response.put("Error", e.getMessage());
//            return ResponseEntity.status(500).body(response);
//        }
//    }

    @GetMapping(value = "/candidate/coding/{candidateId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCodingStatusForCandidate(@PathVariable Long candidateId) {
        System.out.println("=== CALLED getCodingStatusForCandidate with candidateId: " + candidateId);

        // 1. Debug: Show ALL sessions for this candidate
        List<InterviewSession> allSessions = sessionRepo.findByCandidateId(candidateId);
        System.out.println("=== ALL SESSIONS FOR CANDIDATE " + candidateId + " (total: " + allSessions.size() + ") ===");
        if (allSessions.isEmpty()) {
            System.out.println(" → No sessions exist at all for this candidate.");
        } else {
            allSessions.forEach(s -> {
                System.out.println("   → Session ID: " + s.getId() +
                        " | Job: " + s.getJobId() +
                        " | Type: '" + s.getType() + "'" +
                        " | Status: '" + s.getStatus() + "'" +
                        " | Coding Questions: " + (s.getCodingQuestions() != null ? s.getCodingQuestions().size() : 0));
            });
        }

        // 2. Find active CODING sessions (case-insensitive type, only PENDING/STARTED)
        List<InterviewSession> codingSessions = sessionRepo.findByCandidateIdAndTypeIgnoreCaseAndStatusIn(
                candidateId,
                "CODING",
                Arrays.asList("PENDING", "STARTED")
        );

        System.out.println("=== ACTIVE CODING SESSIONS FOUND: " + codingSessions.size());

        if (codingSessions.isEmpty()) {
            System.out.println(" → NO ACTIVE CODING SESSION FOUND for candidate " + candidateId);
            return ResponseEntity.ok(Map.of(
                    "hasCoding", false,
                    "message", "No pending or started coding interview found"
            ));
        }

        // Take the first active session
        InterviewSession activeSession = codingSessions.get(0);

        // Get full questions list
        List<CodingQuestion> questions = activeSession.getCodingQuestions();
        System.out.println(" → USING SESSION: ID=" + activeSession.getId() +
                ", Job=" + activeSession.getJobId() +
                ", Status=" + activeSession.getStatus() +
                ", Coding Questions count=" + questions.size());

        // Return full data including questions array
        Map<String, Object> response = new HashMap<>();
        response.put("hasCoding", true);
        response.put("jobId", activeSession.getJobId());
        response.put("sessionId", activeSession.getId());
        response.put("status", activeSession.getStatus());
        response.put("codingQuestionsCount", questions.size());
        response.put("codingQuestions", questions);  // Now safe with cycle broken

        return ResponseEntity.ok(response);
    }
//
//    @GetMapping("/candidate/mcq/{candidateId}")
//    public ResponseEntity<?> getCandidateMCQTest(@PathVariable Long candidateId) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            // DEBUG: Print all sessions for this candidate
//            List<InterviewSession> allSessions = sessionRepo.findByCandidateId(candidateId);
//            System.out.println("=== ALL SESSIONS FOR CANDIDATE " + candidateId + " ===");
//            allSessions.forEach(s -> {
//                System.out.println("ID: " + s.getId() +
//                        " | JobId: " + s.getJobId() +
//                        " | Type: '" + s.getType() + "'" +
//                        " | Status: '" + s.getStatus() + "'" +
//                        " | Questions: " + s.getQuestionIds());
//            });
//
//            // FORCE FIND MCQ SESSION — IGNORE CASE + DEBUG
//            Optional<InterviewSession> sessionOpt = allSessions.stream()
//                    .filter(s -> s.getType() != null && s.getType().trim().equalsIgnoreCase("MCQ"))
//                    .filter(s -> s.getStatus() != null &&
//                            (s.getStatus().trim().equalsIgnoreCase("PENDING") ||
//                                    s.getStatus().trim().equalsIgnoreCase("STARTED")))
//                    .filter(s -> s.getQuestionIds() != null && !s.getQuestionIds().isEmpty())
//                    .findFirst();
//
//            if (sessionOpt.isEmpty()) {
//                System.out.println("NO VALID MCQ SESSION FOUND FOR CANDIDATE " + candidateId);
//                response.put("hasMCQ", false);
//                return ResponseEntity.ok(response);
//            }
//
//            InterviewSession session = sessionOpt.get();
//            System.out.println("FOUND MCQ SESSION: " + session.getId() + " for Job " + session.getJobId());
//
//            List<MCQ> questions = mcqRepo.findAllById(session.getQuestionIds());
//
//            response.put("hasMCQ", true);
//            response.put("sessionId", session.getId());
//            response.put("jobId", session.getJobId());
//            response.put("questions", questions);
//            response.put("timeLimit", 1800);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.put("hasMCQ", false);
//            response.put("Error", e.getMessage());
//            return ResponseEntity.status(500).body(response);
//        }
//    }

    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleInterview(
            @RequestBody InterviewCreateDTO dto,
            @RequestParam Long recruiterId) {

        try {
            InterviewSession session = interviewSessionService.scheduleInterview(dto, recruiterId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Interview scheduled successfully",
                    "sessionId", session.getId(),
                    "type", session.getType(),
                    "scheduledAt", session.getScheduledAt(),
                    "inviteLink", session.getInviteLink()
            ));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "error", e.getMessage()
                    ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "error", "Failed to schedule interview: " + e.getMessage()
                    ));
        }
    }

    @PostMapping("/{sessionId}/submit-mcq")
    public ResponseEntity<?> submitMCQ(
            @PathVariable Long sessionId,
            @RequestBody Map<String, List<Map<String, String>>> body) {

        Map<String, Object> res = new HashMap<>();

        try {
            InterviewSession session = sessionRepo.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Session not found"));

            if ("SUBMITTED".equals(session.getStatus())) {
                res.put("msg", "Already submitted");
                return ResponseEntity.badRequest().body(res);
            }

            List<Map<String, String>> answers = body.get("answers");
            List<MCQ> questions = mcqRepo.findAllById(session.getQuestionIds());
            int correct = 0;

            for (int i = 0; i < questions.size(); i++) {
                String userAns = answers.get(i).get("answer");
                if (userAns != null && userAns.trim().equals(questions.get(i).getAnswer())) {
                    correct++;
                }
            }

            double score = questions.size() > 0 ? (correct * 100.0 / questions.size()) : 0;

            InterviewResponse response = new InterviewResponse();
            response.setSessionId(sessionId);
            response.setCandidateId(session.getCandidateId());
            response.setType("MCQ");
            response.setAnswersJson(new Gson().toJson(answers));
            response.setScore(score);
            response.setSubmittedAt(LocalDateTime.now());
            response.setStatus("SUBMITTED");

            responseRepo.save(response);

            session.setStatus("SUBMITTED");
            session.setEndedAt(LocalDateTime.now());
            sessionRepo.save(session);

            res.put("msg", "Test submitted! Your score: " + Math.round(score) + "%");
            res.put("score", Math.round(score));
            res.put("correct", correct);
            res.put("total", questions.size());
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            e.printStackTrace();
            res.put("Error", e.getMessage());
            return ResponseEntity.status(500).body(res);
        }
    }

    @PostMapping("/coding/submit/{sessionId}")
    public ResponseEntity<?> submitCodingRound(
            @PathVariable Long sessionId,
            @RequestBody Map<String, String> payload) {

        try {
            // 1. Find session
            InterviewSession session = sessionRepo.findById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

            // 2. Validate it's a CODING session
            if (!"CODING".equals(session.getType())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Not a coding session"
                ));
            }

            // 3. Get submitted code & language
            String candidateCode = payload.get("code");
            String language = payload.getOrDefault("language", "java").toLowerCase();

            if (candidateCode == null || candidateCode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Code is required"
                ));
            }

            // 4. Get the coding question (assuming one for simplicity)
            if (session.getCodingQuestions() == null || session.getCodingQuestions().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "No coding question assigned to this session"
                ));
            }

            CodingQuestion question = session.getCodingQuestions().get(0); // first question

            // 5. Run tests
            int passed = 0;
            int total = question.getTestCases().size();
            List<Map<String, Object>> details = new ArrayList<>();

            String pistonLanguage = switch (language) {
                case "javascript" -> "js";
                case "c++" -> "c++";
                case "c" -> "c";
                case "python" -> "python";
                default -> "java";
            };

            for (TestCase tc : question.getTestCases()) {
                String fullCode = prepareFullCode(candidateCode, question.getFunctionSignature(), tc.getInput(), language);

                Map<String, Object> execResult = executeWithPiston(pistonLanguage, fullCode);

                String actualOutput = (String) execResult.get("output");
                boolean testPassed = actualOutput != null &&
                        actualOutput.trim().equals(tc.getExpectedOutput().trim());

                if (testPassed) passed++;

                // Build detail for each test
                Map<String, Object> testDetail = new HashMap<>();
                testDetail.put("input", tc.isHidden() ? "[Hidden]" : tc.getInput());
                testDetail.put("expected", tc.isHidden() ? "[Hidden]" : tc.getExpectedOutput());
                testDetail.put("actual", actualOutput != null ? actualOutput.trim() : "[No output]");
                testDetail.put("passed", testPassed);
                testDetail.put("error", execResult.get("error"));
                details.add(testDetail);
            }

            // 6. Calculate score
            double score = total > 0 ? (passed * 100.0 / total) : 0.0;

            // 7. Save response
            InterviewResponse response = new InterviewResponse();
            response.setSessionId(sessionId);
            response.setCandidateId(session.getCandidateId());
            response.setType("CODING");
            response.setCode(candidateCode);
            response.setScore(score);
            response.setSubmittedAt(LocalDateTime.now());
            response.setStatus("SUBMITTED");
            responseRepo.save(response);

            // 8. Update session
            session.setStatus("SUBMITTED");
            session.setEndedAt(LocalDateTime.now());
            sessionRepo.save(session);

            // 9. Return proper response (this is what was missing/empty)
            Map<String, Object> finalResult = new HashMap<>();
            finalResult.put("score", Math.round(score * 100) / 100.0); // round to 2 decimals
            finalResult.put("passed", passed);
            finalResult.put("total", total);
            finalResult.put("details", details);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Coding round submitted successfully",
                    "result", finalResult
            ));

        } catch (Exception e) {
            // Log the error so you can see it in console
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "Failed to submit: " + e.getMessage()
            ));
        }
    }

    private String prepareFullCode(String candidateCode, String signature, String input, String language) {
        language = language.toLowerCase().trim();

        // Extract function name only if it's a real function
        String funcName = null;
        if (signature != null && !signature.trim().isEmpty()) {
            // Look for patterns like "def name(", "function name(", "public static String name("
            Pattern pattern = Pattern.compile("(\\w+)\\s*\\(");
            Matcher matcher = pattern.matcher(signature);
            if (matcher.find()) {
                funcName = matcher.group(1).trim();
            }
        }

        // Skip wrapper if:
        // - No valid function name
        // - Signature looks like print/console.log
        // - Signature is empty or just "console.log()"
        boolean isPrintOnly = funcName == null ||
                funcName.isEmpty() ||
                signature.toLowerCase().contains("console.log") ||
                signature.toLowerCase().contains("print");

        if (isPrintOnly) {
            // For print-only questions → return raw candidate code (no extra call)
            return candidateCode;
        }

        // Normal function wrapper (only if real function exists)
        if (language.equals("python")) {
            return candidateCode + "\n\n" +
                    "result = " + funcName + "('" + input.replace("'", "\\'") + "')\n" +
                    "print(result)";
        }

        if (language.equals("javascript")) {
            return candidateCode + "\n\n" +
                    "console.log(" + funcName + "('" + input.replace("'", "\\'") + "'));";
        }

        if (language.equals("java")) {
            return "public class Main {\n" +
                    "    " + candidateCode + "\n" +
                    "    public static void main(String[] args) {\n" +
                    "        System.out.println(" + funcName + "(\"" + input.replace("\"", "\\\"") + "\"));\n" +
                    "    }\n" +
                    "}";
        }

        if (language.equals("c")) {
            return "#include <stdio.h>\n" +
                    "#include <string.h>\n\n" +
                    candidateCode + "\n\n" +
                    "int main() {\n" +
                    "    char input[] = \"" + input.replace("\"", "\\\"") + "\";\n" +
                    "    printf(\"%s\\n\", " + funcName + "(input));\n" +
                    "    return 0;\n" +
                    "}";
        }

        if (language.equals("c++")) {
            return "#include <iostream>\n" +
                    "#include <string>\n" +
                    "using namespace std;\n\n" +
                    candidateCode + "\n\n" +
                    "int main() {\n" +
                    "    string input = \"" + input.replace("\"", "\\\"") + "\";\n" +
                    "    cout << " + funcName + "(input) << endl;\n" +
                    "    return 0;\n" +
                    "}";
        }

        // Fallback for unknown language
        return candidateCode;
    }

    private Map<String, Object> executeWithPiston(String language, String code) {
        RestTemplate rest = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        body.put("language", language.equals("javascript") ? "js" : language);
        body.put("version", "*");

        Map<String, String> file = new HashMap<>();
        file.put("content", code);
        body.put("files", List.of(file));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<Map> response = rest.postForEntity(
                    "https://emkc.org/api/v2/piston/execute",
                    new HttpEntity<>(body, headers),
                    Map.class
            );

            Map run = (Map) response.getBody().get("run");
            Map<String, Object> r = new HashMap<>();
            r.put("output", run.get("output"));
            r.put("error", run.get("stderr"));
            r.put("success", run.get("code") == Integer.valueOf(0));
            return r;

        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("error", e.getMessage());
            return err;
        }
    }

//    @GetMapping("/session/{sessionId}")
//    public ResponseEntity<?> getSessionForCandidate(
//            @PathVariable Long sessionId,
//            @RequestParam Long candidateId) {
//
//        Optional<InterviewSession> opt = sessionRepo.findById(sessionId);
//        if (opt.isEmpty()) {
//            return ResponseEntity.status(404).body(Map.of("error", "Session not found"));
//        }
//
//        InterviewSession session = opt.get();
//
//        if (!session.getCandidateId().equals(candidateId)) {
//            return ResponseEntity.status(403).body("Unauthorized");
//        }
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("id", session.getId());
//        response.put("jobId", session.getJobId());
//        response.put("type", session.getType());
//        response.put("status", session.getStatus());
//        response.put("candidateId", session.getCandidateId());
//
//        // Load CODING questions from JOIN TABLE
//        if ("CODING".equalsIgnoreCase(session.getType())) {
//            // Query the join table directly
//            List<Long> questionIds = interviewSessionCodingQuestionIdsRepository
//                    .findCodingQuestionIdsBySessionId(sessionId);
//
//            if (questionIds != null && !questionIds.isEmpty()) {
//                List<CodingQuestion> questions = codingRepo.findAllById(questionIds);
//                response.put("codingQuestions", questions);
//                System.out.println("Loaded " + questions.size() + " coding questions from join table for session " + sessionId);
//            } else {
//                System.out.println("No coding questions in join table for session " + sessionId);
//            }
//        }
//
//        return ResponseEntity.ok(response);
//    }

//    @PostMapping("/create")   // or whatever your create mapping is
//    public ResponseEntity<?> createInterviewSession(@RequestBody InterviewCreateDTO dto,
//                                                    @RequestParam Long recruiterId) {   // add if needed
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            // Basic validation
//            if (!jobRepo.existsById(dto.getJobId())) {
//                return ResponseEntity.badRequest().body(Map.of("error", "Job not found"));
//            }
//            if (!candidateRepo.existsById(dto.getCandidateId())) {
//                return ResponseEntity.badRequest().body(Map.of("error", "Candidate not found"));
//            }
//
//            InterviewSession session = new InterviewSession();
//            session.setJobId(dto.getJobId());
//            session.setCandidateId(dto.getCandidateId());
//            session.setType(dto.getType().toUpperCase());
//            session.setScheduledAt(dto.getScheduledAt() != null ? dto.getScheduledAt() : LocalDateTime.now());
//            session.setStatus("PENDING");
//            // Add other fields you need (meetingLink, webcamRequired, etc.)
//
//            if ("CODING".equalsIgnoreCase(session.getType())) {
//                if (dto.getQuestionIds() == null || dto.getQuestionIds().isEmpty()) {
//                    return ResponseEntity.badRequest().body(Map.of("error", "Question IDs required for CODING interview"));
//                }
//
//                List<CodingQuestion> selectedQuestions = codingRepo.findAllById(dto.getQuestionIds());
//
//                if (selectedQuestions.size() != dto.getQuestionIds().size()) {
//                    return ResponseEntity.badRequest().body(Map.of("error", "One or more coding questions not found"));
//                }
//
//                session.setCodingQuestions(selectedQuestions);   // ← This saves to join table
//            } else {
//                // For MCQ / HR
//                session.setQuestionIds(dto.getQuestionIds() != null ? dto.getQuestionIds() : new ArrayList<>());
//            }
//
//            InterviewSession saved = sessionRepo.save(session);
//
//            response.put("message", "Interview session created");
//            response.put("sessionId", saved.getId());
//            response.put("type", saved.getType());
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.put("error", e.getMessage());
//            return ResponseEntity.status(500).body(response);
//        }
//    }

    // ────────────────────────────────────────────────
// GET SINGLE SESSION (with coding questions)
// ────────────────────────────────────────────────
//    @GetMapping("/{sessionId}")
//    public ResponseEntity<?> getInterviewSession(@PathVariable Long sessionId) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        InterviewSession session = sessionRepo.findById(sessionId)
//                .orElse(null);
//
//        if (session == null) {
//            response.put("error", "Session not found");
//            return ResponseEntity.notFound().build();
//        }
//
//        response.put("id", session.getId());
//        response.put("jobId", session.getJobId());
//        response.put("candidateId", session.getCandidateId());
//        response.put("type", session.getType());
//        response.put("status", session.getStatus());
//        response.put("scheduledAt", session.getScheduledAt());
//        response.put("meetingLink", session.getMeetingLink());
//
//        if ("CODING".equalsIgnoreCase(session.getType())) {
//            // Because we used fetch = FetchType.EAGER → already loaded
//            response.put("codingQuestions", session.getCodingQuestions());
//            // Debug line (remove later)
//            System.out.println("Coding questions count: " + session.getCodingQuestions().size());
//        } else {
//            response.put("questionIds", session.getQuestionIds());
//        }
//
//        return ResponseEntity.ok(response);
//    }


    @GetMapping("/session")
    public ResponseEntity<?> getSessionByParams(
            @RequestParam Long jobId,
            @RequestParam Long candidateId,
            @RequestParam String type) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<InterviewSession> sessions = sessionRepo.findByCandidateIdAndTypeIgnoreCaseAndStatusIn(
                    candidateId,
                    type,
                    List.of("PENDING", "STARTED")
            );

            InterviewSession session = sessions.stream()
                    .filter(s -> s.getJobId().equals(jobId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No active session found for this job and type"));

            response.put("session", session);  // Now safe with cycle broken

            if ("CODING".equalsIgnoreCase(type)) {
                System.out.println("Returning coding questions: " + session.getCodingQuestions().size());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/create")   // or /recruiter/interviews or wherever it is
    @Transactional
    public ResponseEntity<?> createInterview(@RequestBody InterviewCreateDTO dto) {
        System.out.println("=== CREATE INTERVIEW START ===");
        System.out.println("DTO received: " + dto);  // ← very important

        try {
            InterviewSession session = new InterviewSession();
            session.setJobId(dto.getJobId());
            session.setCandidateId(dto.getCandidateId());
            session.setType(dto.getType().toUpperCase());
            session.setStatus("PENDING");
            session.setScheduledAt(dto.getScheduledAt() != null ? dto.getScheduledAt() : LocalDateTime.now());

            System.out.println("Session before any logic: " + session);

            if ("CODING".equals(session.getType())) {
                System.out.println("Entering CODING branch");
                if (dto.getQuestionIds() == null || dto.getQuestionIds().isEmpty()) {
                    System.out.println("WARNING: No questionIds for CODING");
                }
                List<CodingQuestion> questions = codingRepo.findAllById(dto.getQuestionIds());
                System.out.println("Found " + questions.size() + " coding questions");
                session.setCodingQuestions(questions);
                // IMPORTANT: save AFTER setting collection
                session = sessionRepo.saveAndFlush(session);  // flush forces write
                System.out.println("CODING session saved with ID: " + session.getId());
            } else if ("MCQ".equals(session.getType())) {
                System.out.println("Entering MCQ branch");
                session.setQuestionIds(dto.getQuestionIds() != null ? dto.getQuestionIds() : new ArrayList<>());
                session = sessionRepo.saveAndFlush(session);
                System.out.println("MCQ session saved with ID: " + session.getId());
            } else {
                System.out.println("Unknown type → fallback save");
                session = sessionRepo.saveAndFlush(session);
            }

            System.out.println("=== CREATE INTERVIEW SUCCESS - ID: " + session.getId() + " ===");
            return ResponseEntity.ok(Map.of(
                    "msg", "Interview scheduled",
                    "sessionId", session.getId(),
                    "inviteLink", "..." // whatever
            ));

        } catch (Exception e) {
            System.out.println("!!! CREATE INTERVIEW FAILED !!!");
            e.printStackTrace();   // ← MUST print full stack trace
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/candidate/mcq/{candidateId}")
    public ResponseEntity<?> getPendingMCQ(@PathVariable Long candidateId) {
        System.out.println("=== GET PENDING MCQ → candidateId: " + candidateId);

        List<InterviewSession> sessions = sessionRepo.findByCandidateIdAndTypeIgnoreCaseAndStatusIn(
                candidateId, "MCQ", List.of("PENDING", "STARTED"));

        if (sessions.isEmpty()) {
            return ResponseEntity.ok(Map.of("hasMCQ", false));
        }

        InterviewSession session = sessions.get(0);
        List<MCQ> questions = mcqRepo.findAllById(session.getQuestionIds());

        System.out.println("Returning " + questions.size() + " MCQ questions for session " + session.getId());

        Map<String, Object> res = new HashMap<>();
        res.put("hasMCQ", true);
        res.put("sessionId", session.getId());
        res.put("jobId", session.getJobId());
        res.put("timeLimit", 1800);
        res.put("questions", questions);

        return ResponseEntity.ok(res);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<?> getSession(@PathVariable Long sessionId,
                                        @RequestParam Long candidateId) {
        Optional<InterviewSession> opt = sessionRepo.findById(sessionId);
        if (opt.isEmpty() || !opt.get().getCandidateId().equals(candidateId)) {
            return ResponseEntity.status(404).body(Map.of("error", "Session not found or unauthorized"));
        }

        InterviewSession session = opt.get();

        Map<String, Object> res = new HashMap<>();
        res.put("id", session.getId());
        res.put("type", session.getType());
        res.put("status", session.getStatus());
        res.put("jobId", session.getJobId());
        res.put("scheduledAt", session.getScheduledAt());

        if ("CODING".equalsIgnoreCase(session.getType())) {
            res.put("codingQuestions", session.getCodingQuestions());  // Now safe
        } else {
            res.put("questionIds", session.getQuestionIds());
        }

        return ResponseEntity.ok(res);
    }
}