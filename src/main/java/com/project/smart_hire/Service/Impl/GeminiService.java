package com.project.smart_hire.Service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeminiService {

//    @Value("${app.gemini.api-key}")
//    private String apiKey;
//
//    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=";
//    private static final int MAX_RETRIES = 3;
//    private static final long RETRY_DELAY_MS = 5000;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    // Parse Resume (unchanged)
//    public String parseResume(MultipartFile file) throws IOException {
//        Path tempFile = Files.createTempFile("resume_", ".pdf");
//        file.transferTo(tempFile);
//        byte[] bytes = Files.readAllBytes(tempFile);
//        String base64 = Base64.getEncoder().encodeToString(bytes);
//
//        String prompt = """
//                Extract in JSON only:
//                {
//                  "name": "",
//                  "email": "",
//                  "phone": "",
//                  "skills": [],
//                  "experience": "",
//                  "education": ""
//                }
//                Resume (Base64): %s
//                """.formatted(base64);
//
//        String response = callGemini(prompt);
//        Files.deleteIfExists(tempFile);
//        return response;
//    }
//
//    // Generate MCQs (unchanged)
//    public String generateMCQs(String jobRole, int count) {
//        String prompt = String.format(
//                "Generate %d MCQs for %s developer. Return ONLY JSON array:\n" +
//                        "[{\"question\":\"...\",\"options\":[\"A\",\"B\",\"C\",\"D\"],\"answer\":\"A\"}]",
//                count, jobRole);
//
//        return callGemini(prompt);
//    }
//
//    // Reusable Gemini Caller (unchanged)
//    private String callGemini(String prompt) {
//        int attempt = 0;
//        while (attempt < MAX_RETRIES) {
//            try {
//                Map<String, Object> contentPart = new HashMap<>();
//                contentPart.put("text", prompt);
//
//                Map<String, Object> content = new HashMap<>();
//                content.put("parts", new Object[]{contentPart});
//
//                Map<String, Object> body = new HashMap<>();
//                body.put("contents", new Object[]{content});
//
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_JSON);
//
//                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//
//                ResponseEntity<String> response = restTemplate.exchange(
//                        GEMINI_ENDPOINT + apiKey,
//                        HttpMethod.POST,
//                        entity,
//                        String.class
//                );
//
//                return parseGeminiResponse(response.getBody());
//
//            } catch (HttpClientErrorException.TooManyRequests e) {
//                attempt++;
//                if (attempt >= MAX_RETRIES) {
//                    throw new RuntimeException("Gemini API quota exceeded after " + MAX_RETRIES + " retries", e);
//                }
//                try { Thread.sleep(RETRY_DELAY_MS); }
//                catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
//            } catch (Exception e) {
//                throw new RuntimeException("Gemini API error: " + e.getMessage(), e);
//            }
//        }
//        throw new RuntimeException("Failed after " + MAX_RETRIES + " retries");
//    }
//
//    // Parse Response (unchanged)
//    private String parseGeminiResponse(String json) {
//        try {
//            JsonNode root = objectMapper.readTree(json);
//            JsonNode textNode = root.path("candidates").get(0).path("content").path("parts").get(0).path("text");
//            if (textNode.isMissingNode()) {
//                throw new RuntimeException("Invalid Gemini response format");
//            }
//            return textNode.asText().trim();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage(), e);
//        }
//    }
}