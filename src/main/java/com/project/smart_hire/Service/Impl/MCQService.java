package com.project.smart_hire.Service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.smart_hire.Entity.MCQ;
import com.project.smart_hire.Repository.MCQRepository;
import com.project.smart_hire.Service.IMCQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MCQService implements IMCQService {

    @Autowired private MCQRepository repo;
    @Autowired private RestTemplate restTemplate;
    @Autowired private ObjectMapper objectMapper;

    @Value("${app.gemini.api-key}")
    private String API_KEY;

    private static final String GEMINI_API =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

    @Override
    public List<MCQ> generateMCQs(String topic, int count) {
        // Ultra-strict prompt to force clean JSON output
        String userPrompt = """
            Generate exactly %d multiple choice questions on the topic: "%s".
            
            RULES - FOLLOW EXACTLY:
            1. Output ONLY a valid JSON array
            2. NO explanations, NO markdown, NO ```json blocks
            3. Start directly with [ and end with ]
            4. Use this exact structure:
               {"question":"...","optionA":"...","optionB":"...","optionC":"...","optionD":"...","answer":"A"}
            5. Answer letters must be distributed (A, B, C, D)
            6. Options must be plausible, only one correct
            
            Return exactly %d questions.
            """.formatted(count, topic, count);

        // Use system + user roles for maximum compliance
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
                Map.of("role", "user", "parts", List.of(Map.of("text",
                        """
                        YOU ARE A STRICT JSON-ONLY GENERATOR. NEVER ADD ANY TEXT OUTSIDE JSON.
                        
                        Generate exactly %d multiple choice questions on "%s".
                        Return ONLY a valid JSON array with no markdown, no ```json, no explanation.
                        Start with [ and end with ]
                        
                        Structure must be exactly:
                        {"question":"...","optionA":"...","optionB":"...","optionC":"...","optionD":"...","answer":"A"}
                        
                        Distribute correct answers across A, B, C, D.
                        Make options plausible. Only one correct answer per question.
                        
                        Example:
                        [{"question":"What is Java?","optionA":"A coffee","optionB":"A language","optionC":"An island","optionD":"A framework","answer":"B"}]
                        
                        Now generate exactly %d questions on "%s":
                        """.formatted(count, topic, count, topic)
                )))
        ));

        // NO responseMimeType → avoids TLS handshake failure on restricted networks
        requestBody.put("generationConfig", Map.of(
                "temperature", 0.1,
                "topP", 0.8,
                "maxOutputTokens", 8192
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            String apiUrl = GEMINI_API + API_KEY;
            System.out.println("=== CALLING GEMINI API ===");
            System.out.println("URL: " + apiUrl);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseGeminiResponse(response.getBody());
            } else {
                System.out.println("API Error: " + response.getStatusCode() + " - " + response.getBody());
                return List.of();
            }
        } catch (Exception e) {
            System.out.println("=== ERROR GENERATING MCQs ===");
            e.printStackTrace();
            return List.of();
        }
    }

    private List<MCQ> parseGeminiResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode parts = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts");

            if (parts.isEmpty()) {
                System.out.println("No parts found in response");
                return List.of();
            }

            String rawText = parts.get(0).path("text").asText().trim();
            System.out.println("=== RAW GEMINI TEXT ===");
            System.out.println(rawText);
            System.out.println("=== END RAW ===");

            String jsonArray = extractJsonArray(rawText);
            if (jsonArray == null || jsonArray.trim().isEmpty()) {
                System.out.println("Failed to extract valid JSON array");
                return List.of();
            }

            MCQ[] mcqs = objectMapper.readValue(jsonArray, MCQ[].class);
            List<MCQ> result = Arrays.asList(mcqs);

            System.out.println("Successfully generated " + result.size() + " MCQs");
            return result;

        } catch (Exception e) {
            System.out.println("Parsing failed:");
            e.printStackTrace();
            return List.of();
        }
    }

    private String extractJsonArray(String text) {
        if (text == null || text.isEmpty()) return null;

        text = text.trim();

        // Remove common wrappers
        if (text.startsWith("```json")) text = text.substring(7);
        if (text.startsWith("```")) text = text.substring(3);
        if (text.endsWith("```")) text = text.substring(0, text.length() - 3);
        text = text.trim();

        int start = text.indexOf('[');
        int end = text.lastIndexOf(']');

        if (start == -1 || end == -1 || end <= start) {
            System.out.println("No JSON array brackets found");
            return null;
        }

        String candidate = text.substring(start, end + 1);

        // Validate it's proper JSON
        try {
            objectMapper.readTree(candidate);
            return candidate;
        } catch (Exception e) {
            System.out.println("Extracted text is not valid JSON: " + candidate);
            return null;
        }
    }

    // === CRUD METHODS (unchanged) ===
    @Override
    public List<MCQ> findByRecruiterId(Long recruiterId) {
        return repo.findByRecruiterId(recruiterId);
    }

    public MCQ saveMCQ(MCQ mcq, Long recruiterId) {
        mcq.setRecruiterId(recruiterId);
        return repo.save(mcq);
    }

    @Override
    public List<MCQ> getAllMCQs() {
        return repo.findAll();
    }

    public void deleteMCQ(Long id) {
        repo.deleteById(id);
    }

    public MCQ updateMCQ(Long id, MCQ mcq) {
        MCQ existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        existing.setQuestion(mcq.getQuestion());
        existing.setOptionA(mcq.getOptionA());
        existing.setOptionB(mcq.getOptionB());
        existing.setOptionC(mcq.getOptionC());
        existing.setOptionD(mcq.getOptionD());
        existing.setAnswer(mcq.getAnswer());
        return repo.save(existing);
    }
}