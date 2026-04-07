package com.project.smart_hire.Controller;

import com.project.smart_hire.Service.Impl.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin("*")
public class QuestionController {

    @Autowired
    private GeminiService geminiService;

    // Recruiter: Generate Questions using Gemini
//    @PostMapping("/generate")
//    public ResponseEntity<?> generateQuestions(
//            @RequestParam String jobRole,
//            @RequestParam(defaultValue = "5") int count) {
//
//        HashMap<String, Object> response = new HashMap<>();
//
//        try {
//            String jsonQuestions = geminiService.generateMCQs(jobRole, count);
//            response.put("msg", "Questions generated successfully");
//            response.put("questions", jsonQuestions);
//            return ResponseEntity.status(HttpStatus.OK).body(response);
//
//        } catch (RuntimeException e) {
//            response.put("Error", "AI service error: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        } catch (Exception e) {
//            response.put("Error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//    }
}