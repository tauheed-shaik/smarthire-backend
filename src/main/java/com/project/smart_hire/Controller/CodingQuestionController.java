package com.project.smart_hire.Controller;

import com.project.smart_hire.Entity.CodingQuestion;
import com.project.smart_hire.Repository.CodingQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coding")
@CrossOrigin(origins = "*")
public class CodingQuestionController {

    @Autowired
    private CodingQuestionRepository codingRepo;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> createCodingQuestion(
            @RequestBody CodingQuestion question,      // JSON body
            @RequestParam Long recruiterId) {

        question.setRecruiterId(recruiterId);
        CodingQuestion saved = codingRepo.save(question);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Coding question created",
                "question", saved
        ));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CodingQuestion>> getMyCodingQuestions(
            @RequestParam Long recruiterId) {
        List<CodingQuestion> questions = codingRepo.findByRecruiterId(recruiterId);
        return ResponseEntity.ok(questions);  // direct array return
    }
}