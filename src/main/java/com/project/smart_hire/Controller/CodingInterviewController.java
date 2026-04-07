package com.project.smart_hire.Controller;

import com.project.smart_hire.DTO.InterviewCreateDTO;
import com.project.smart_hire.Entity.InterviewSession;
import com.project.smart_hire.Service.Impl.CodingInterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/interviews/coding")
@CrossOrigin("*")
public class CodingInterviewController {

    @Autowired
    private CodingInterviewService codingInterviewService;

    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleCodingRound(
            @RequestBody InterviewCreateDTO dto,
            @RequestParam Long recruiterId) {

        try {
            InterviewSession session = codingInterviewService.scheduleCodingRound(dto, recruiterId);

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Coding round scheduled");
            resp.put("sessionId", session.getId());
            resp.put("inviteLink", session.getInviteLink());

            return ResponseEntity.ok(resp);

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", "Internal error"
            ));
        }
    }
}